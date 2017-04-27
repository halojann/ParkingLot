# Create your views here.
from django.http import HttpResponse, JsonResponse
from django.contrib import auth
from django.views.decorators.csrf import csrf_exempt
import sys
sys.path.append('../')
from registration.models import User, ParkingLot
from datetime import datetime, timedelta
import base64
from Crypto.Hash import SHA
from Crypto.Signature import PKCS1_v1_5
from Crypto.PublicKey import RSA
from .models import Transaction 
import json
# Create your views here.

@csrf_exempt
def information(request):
    status=str()
    response = {}
    if request.method == 'POST':
        try:
            data = json.loads(request.body)
        except:
            response['status'] = '101' #request fails
            return JsonResponse(response)
        lotname = data['parkinglot_name']
            #user = authenticate(request, username=usr_name, password=passwd)
        if lotname:
            try:
                lot = ParkingLot.objects.get(lotname=lotname)
                response['total_number'] = lot.total_number
                response['price_info'] = lot.price_info
                response['address'] = lot.address
                
                start_hour = str((lot.start_time.hour-4)%24) if lot.start_time.hour!=4 else '00'
#                 start_hour = start_hour if start_hour>=0 else start_hour+24
                close_hour = str((lot.close_time.hour-4)%24) if lot.close_time.hour!=4 else '00'
#                 close_hour = close_hour if close_hour>=0 else close_hour+24
                start_minute = str(lot.start_time.minute-56) if lot.start_time.minute!=56 else '00'
                close_minute = str(lot.close_time.minute-56) if lot.close_time.minute!=56 else '00'
                response['start_time'] = start_hour + ":" + start_minute
                response['close_time'] = close_hour + ":" + close_minute
                response['email'] = lot.email
                response['phone'] = lot.phone           
                now = datetime.now()
                
                remaining_dict = json.loads(lot.remaining_number)
                remaining_number = remaining_dict[str(now.hour)]
                response['remaining_number'] = remaining_number
                response['status'] = '11' #successful
            except:
                response['status'] = '00' # not exist
        else:
            response['status'] = '00' # no lot name was received
    return JsonResponse(response)

@csrf_exempt
def reserve(request):
    status=str()
    response = {}
    if request.method == 'POST':
        try:
            data = json.loads(request.body)
        except:
            response['status'] = '10'
            return JsonResponse(response)
        username = data['username']
        lotname = data['parkinglot_name']
        duration = data['duration']# unit is minute, integer
        start = datetime.now()
        end = start + timedelta(minutes=int(duration))
        
        try:
            user = User.objects.get(username=username)
            lot = ParkingLot.objects.get(lotname=lotname)
        except:
            response['status'] = '002' # no lot name was received or no such lotname
            return JsonResponse(response)
        if end.day == start.day and end.hour - (lot.close_time.hour-4)%24 < 0 and start.hour - (lot.start_time.hour-4)%24 >=0: # duration is proper before close time

            remaining_dict = json.loads(lot.remaining_number)
            remaining = int(remaining_dict[str(start.hour)])
#             return HttpResponse(remaining_number)
            if remaining > 0:                   
                #update the database
                remaining -= 1
                remaining_dict[str(start.hour)] = str(remaining)
                lot.remaining_number = json.dumps(remaining_dict)
                lot.save() 
#                     uid = User.objects.get(username=username).uid
#                     pid = lot.pid
                #reservation = Transaction.objects.create(uid=uid, pid=pid, start_time=start, end_time=end)
                reservation = lot.transaction_set.create(start_time=start, end_time=end, user=user, lot=lot)
                reservation.user.is_involved=True
                reservation.user.save()
                reservation.save()
                #generate a token
                with open('user_service/master-private.pem') as f:
                    message = username + lotname + str(start) + str(end) + str(reservation.transaction_no)
                    rsakey = RSA.importKey(f.read())
                    digest = SHA.new()
                    digest.update(message)
                    signature = base64.b64encode(PKCS1_v1_5.new(rsakey).sign(digest))
                response['transaction_no'] = reservation.transaction_no
                response['token'] = signature
                response['start'] = str(start)
                response['end'] = str(end)
                response['status'] = '11'
            else:
                response['status'] = '00' # no remaining space                   
        else:
            response['status'] = '001' # cannot reserve so long time
    return JsonResponse(response)

# @csrf_exempt
# def arrive(request):
#     return
# @csrf_exempt
# def leave(request):
#     return