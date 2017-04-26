from django.shortcuts import render

# Create your views here.
from django.http import HttpResponseRedirect, HttpResponse
from django.contrib.auth.decorators import login_required
#from django.contrib.auth import authenticate, login as auth_login, logout as auth_logout
from django.contrib import auth
from django.shortcuts import render, get_object_or_404, render_to_response
from django.urls import reverse
from django.views import generic
from django.utils import timezone
from django import forms
from django.template import RequestContext
from django.views.decorators.csrf import csrf_exempt
import sys
sys.path.append('../')
from registration.models import User, ParkingLot
from datetime import datetime, timedelta
import base64
from Crypto.Hash import SHA
from Crypto.Signature import PKCS1_PSS
from Crypto.PublicKey import RSA
# Create your views here.

@csrf_exempt
def information(request):
    status=str()
    response = {}
    if request.method == 'POST':
        try:
            data = json.loads(request)
        except:
            response['status'] = '10'
            return JsonResponse(response)
        lotname = data['parkinglot_name']
            #user = authenticate(request, username=usr_name, password=passwd)
        if lotname:
            lot = ParkingLot.objects.get(lotname=lotname)
            response['total_number'] = lot.total_number
            response['price_info'] = lot.price_info
            response['start_time'] = lot.start_time
            response['close_time'] = lot.close_time
            response['email'] = lot.email
            response['phone'] = lot.phone           
            now = datetime.now()
            remaining_dict = json.loads(lot.remaining_number)
            remaining_number = remaining_dict[str(now.hour)]
            response['remaining_number'] = remaining_number
            response['status'] = '11' 
        else:
            response['status'] = '00' # no lot name was received
    else:
        response['status'] = '10'
    return JsonResponse(response)

@csrf_exempt
def reserve(request):
    status=str()
    response = {}
    if request.method == 'POST':
        try:
            data = json.loads(request)
        except:
            response['status'] = '10'
            return JsonResponse(response)
        username = data['username']
        lotname = data['parkinglot_name']
        duration = data['duration']# unit is minute, integer
        start = datetime.now()
        end = start + timedelta(minutes=duration)
        try:
            lot = ParkingLot.objects.get(lotname=lotname)
            if (end - lot.close_time).seconds <= 0: # duration is proper before close time
                remaining_dict = json.loads(lot.remaining_number)
                remaining_number = remaining_dict[str(start)]
                if remaining_number > 0:                   
                    #update the database
                    remaining_dict[str(start)] -= 1
                    lot.remaining_number = json.dumps(remaining_dict)
                    lot.save() 
                    #generate a token
                    with open('master-private.pem') as f:
                        message = username + lotname + start + end
                        rsakey = RSA.importKey(f.read())
                        digest = SHA.new()
                        digest.update(message)
                        signature = base64.b64encode(PKCS1_PSS.new(rsakey).sign(digest))
                    response['token'] = signature
                    response['start'] = start
                    response['end'] = end
                    response['status'] = '11'
                else:
                    response['status'] = '00' # no remaining space                   
            else:
                response['status'] = '00' # cannot reserve so long time
        except:
            response['status'] = '00' # no lot name was received or no such lotname
            return JsonResponse(response)
    else:
        response['status'] = '10'
    return JsonResponse(response)

# @csrf_exempt
# def arrive(request):
#     return
# @csrf_exempt
# def leave(request):
#     return