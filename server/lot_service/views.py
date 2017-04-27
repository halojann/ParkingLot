from django.http import HttpResponse, JsonResponse
from django.contrib import auth
from django.views.decorators.csrf import csrf_exempt
from datetime import datetime
import sys
sys.path.append('../')
from registration.models import User, ParkingLot
from user_service.models import Transaction
import json
from datetime import datetime
# Create your views here.

@csrf_exempt
def arrive(request):
    status=str()
    response = {}
    if request.method == 'POST':
        try:
            data = json.loads(request.body)
        except:
            response['status'] = '101' # request fails
            return JsonResponse(response)
#         username_lot = data['username_lot']
#         username_user = data['username_user']
        transaction_no = data['transaction_no']
        if transaction_no:
            reservation = Transaction.objects.get(transaction_no=transaction_no)
            arrive = datetime.now()
#             lot = reservation.lot
#             user = reservation.user
            reservation.arrive_time = arrive
            reservation.save()
            response['status'] = '11' #successful
        else:
            response['status'] = '10' # request fails
    return JsonResponse(response)


@csrf_exempt
def leave(request):
    status=str()
    response = {}
    if request.method == 'POST':
        try:
            data = json.loads(request.body)
        except:
            response['status'] = '10' # request fails
            return JsonResponse(response)
#         username_lot = data['username_lot']
#         username_user = data['username_user']
        transaction_no = data['transaction_no']
        if transaction_no:
            # update the end_time to the real leave time
            reservation = Transaction.objects.get(transaction_no=transaction_no)
            if reservation.is_active == True:
                leave = datetime.now()
                reservation.leave_time = leave
                reservation.is_active = False
                reservation.user.is_involved = False
                reservation.user.save()
                reservation.save()
                # add 1 to the remaining space
                lot = reservation.lot
                remaining_dict = json.loads(lot.remaining_number)
                remaining = int(remaining_dict[str(leave.hour)]) + 1
                remaining_dict[str(leave.hour)] = str(remaining)
                lot.remaining_number = json.dumps(remaining_dict)
                lot.save()
                response['status'] = '11' #successful
            else:
                response['status'] = '01' # repeated request, this transaction has already finished
        else:
            response['status'] = '10' # request fails
    return JsonResponse(response)
