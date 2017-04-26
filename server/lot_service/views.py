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
from datetime import datetime
import sys
sys.path.append('../')
from registration.models import User, ParkingLot
from user_service import Reserving
# Create your views here.

@csrf_exempt
def arrive(request):
    status=str()
    response = {}
    if request.method == 'POST':
        try:
            data = json.loads(request)
        except:
            response['status'] = '10' # request fails
            return JsonResponse(response)
#         username_lot = data['username_lot']
#         username_user = data['username_user']
        transaction_no = data['transaction_no']
        if transaction_no:
            reservation = Reserving.objects.get(transaction_no=transaction_no)
            start = datatime.now()
#             lot = reservation.lot
#             user = reservation.user
            reservation.start_time = start
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
            data = json.loads(request)
        except:
            response['status'] = '10' # request fails
            return JsonResponse(response)
#         username_lot = data['username_lot']
#         username_user = data['username_user']
        transaction_no = data['transaction_no']
        if transaction_no:
            # update the end_time to the real leave time
            reservation = Reserving.objects.get(transaction_no=transaction_no)
            end = datatime.now()
            reservation.end_time = end 
            reservation.save()
            # add 1 to the remaining space
            lot = reservation.lot
            remaining_dict = json.loads(lot.remaining_number)
            remaining_dict[str(end.hour)] +=1
            lot.remaining_number = json.dumps(remaining_dict)
            lot.save()
            response['status'] = '11' #successful
        else:
            response['status'] = '10' # request fails
    return JsonResponse(response)
