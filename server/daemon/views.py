from django.http import HttpResponseRedirect, HttpResponse, JsonResponse
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
from datetime import datetime, timedelta
import time
import sys
sys.path.append('../')
from registration.models import User, ParkingLot
from user_service.models import Transaction
import json
import pytz
# Create your views here.

_a = time.strptime('00:00', '%H:%M')
_b = time.strptime('00:00', '%H:%M')
arrive_default = datetime(*_a[:5])
leave_default = datetime(*_b[:5])

# @csrf_exempt
# def autocancel(request):
#     status=str()
#     response = {}
#     if request.method == 'POST':
#         try:
#             data = json.loads(request.body)
#         except:
#             response['status'] = '10' # request fails
#             return JsonResponse(response)
# #         username_lot = data['username_lot']
# #         username_user = data['username_user']
#         username = data['username']
#         password = data['password']
#         if username and password:
#             if request.user.is_authenticated:
#                 return HttpResponse(cancel_if_not_arrive_on_time())
#                 #cancel_if_not_arrive_on_time()
#                 response['status'] = '11' #successful
#             else:                
#                 user = auth.authenticate(username=username, password=password)
#                 if user is not None:
#                     auth.login(request, user)
#                     #cancel_if_not_arrive_on_time()
#                     return HttpResponse(cancel_if_not_arrive_on_time())
#                     response['status'] = '11' #successful
#         else:
#             response['status'] = '10' # request fails
#     return JsonResponse(response)

# def cancel_if_not_arrive_on_time():
#     now = datetime.now()
#     transaction_list = Transaction.objects.filter(is_active=True)
#     if transaction_list is not None:
#         for each in transaction_list:
#             #return each.start_time.replace(tzinfo=None)
#             start = each.start_time + timedelta(hours=-4)
# 
#             waiting_time = (now-start).seconds
#             if waiting_time > 1200 and (each.arrive_time - arrive_default).seconds == 4*3600 + 56*60: # not arrive within 20 mins
#                 #cancel by adding 1 to the remaining space and delete this transaction
#                 lot = each.lot
#                 remaining_dict = json.loads(lot.remaining_number)
#                 for t in range(now.hour, end_time.hour+1):
#                     remaining = int(remaining_dict[str(t)]) + 1
#                     remaining_dict[str(t)] = str(remaining)
#                 lot.remaining_number = json.dumps(remaining_dict)
#                 lot.save()
#                 each.user.is_involved = False
#                 each.delete()
#                 return True     

@csrf_exempt
def dailyreboot(request):
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
        username = data['username']
        password = data['password']
        if username and password:
            if request.user.is_authenticated:
                reset_remaining_if_close()
                response['status'] = '11' #successful
            else:                
                user = auth.authenticate(username=username, password=password)
                if user is not None:
                    auth.login(request, user)
                    reset_remaining_if_close()
                    response['status'] = '11' #successful
        else:
            response['status'] = '10' # request fails
    return JsonResponse(response)

def reset_remaining_if_close():
    now = datetime.now()
    lot_list = ParkingLot.objects.all()
    if lot_list is not None:
        for each in lot_list:
            if (now.hour > (each.close_time.hour-5)%24) or (now.hour == (each.close_time.hour-5) and now.minute > (each.close_time.minute-56)%60): # lot has closed
                #reset all the remaining number to the maximum
                remaining_dict = json.loads(each.remaining_number)
                for t in range(24):
                    remaining_dict[str(t)] = each.total_number
                each.remaining_number = json.dumps(remaining_dict)
                each.save()
