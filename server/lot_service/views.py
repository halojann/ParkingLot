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
            lot = reservation.lot
            user = reservation.user
#             lot = ParkingLot.objects.get(username=username_lot)
#             user = User.objects.get(username=username_user)
            reservation.start_time = start
            reservation.save()
            response['status'] = '11' #successful
        else:
            response['status'] = '10' # request fails
    return JsonResponse(response)


@csrf_exempt
def leave(request):
    
    return
