from django.http import JsonResponse
from django.contrib import auth
from django.views.decorators.csrf import csrf_exempt
import json
from datetime import datetime
import time
from .models import User, ParkingLot

# Create your views here.
@csrf_exempt
def hello(request):
    if request.method == 'POST':
        data = {"content": "hello", "type":"greet" }
        response = json.loads(request.body)
        response['account'] = 'chihaotian'
        response['password'] = '123456'
        #return HttpResponse("hello world")
        #response['LotName'] = 'Temple'
        #reponse = {}
        #response['data'] = request.POST['PostData']
        #name = request.POST.get('account')
        #name = name + "haha"
        
        #return HttpResponse(response['password'])
    
        return JsonResponse(response)

def pwd_validate(p1, p2):
    return p1 == p2

@csrf_exempt
def register_user(request):
    status=str()
    response = {}
    if request.method == 'POST':
        try:
            data = json.loads(request.body)
        except:
            # request fails
            response['status'] = '10'
            return JsonResponse(response)
        username = data['username']
        email = data['email']
        phone = data['phone']
        password = data['password']
        password2 = data['password2']
        if username and password and email and phone and password and password2:
            if not User.objects.all().filter(username=username):
                if pwd_validate(password, password2):
                    user = User.objects.create(username=username, password=password, email=email, phone=phone)
                    user.save()
                    #login_validate(request, username, password)
                    response['status'] = '11'
                else:
                    response['status'] = '01'
            else:
                #The username has existed, please change your username
                response['status'] = '00'
        else:
            # information missing
            response['status'] = '01'
    else:
        response['status'] = '10'
    return JsonResponse(response)

def login_validate(request, user_name, pass_word, table):
    try:
        user = table.objects.get(username=user_name)
        if user.password == pass_word:
            #user_auth = authenticate(username=user_name, password=pass_word)
            #auth.login(request, user)
            return 0
        else:
            return 1
    except:
        return 2
"""        
    if user is not None:
        #if user.is_active:
        auth.login(request, user)
        return True
    return rtvalue
    """
    
@csrf_exempt   
def mylogin_user(request):
    status=str()
    response = {}
    if request.method == 'POST':
        try:
            data = json.loads(request.body)
        except:
            response['status'] = '10'
            return JsonResponse(response)
        username = data['username']
        password = data['password']
            #user = authenticate(request, username=usr_name, password=passwd)
        if username and password:
            result = login_validate(request, username, password, User)
            if result == 0:
                response['status'] = '11'
            else:
                response['status'] = '00'
        else:
            response['status'] = '00'
            #return HttpResponse('2')
            #return render_to_response('registration/registration/login.html', {'error': error, 'form': form}, RequestContext(request))
    else:
        response['status'] = '10'
    return JsonResponse(response)

@csrf_exempt
def changepassword_user(request):
    return

@csrf_exempt
def register_operator(request):
    status=str()
    response = {}
    if request.method == 'POST':
        try:
            data = json.loads(request.body)
        except:
            # request fails
            response['status'] = '10'
            return JsonResponse(response)
        username = data['username']
        password = data['password']
        password2 = data['password2']
        lotname = data['lotname']
        address = data['address']
        total_number = data['total_number']
        remaining_dict = dict(zip([str(each) for each in range(24)], [total_number]*24))
        remaining_number = json.dumps(remaining_dict)
        price_info = data['price_info']
        start_time_str = data['start_time']
        close_time_str = data['close_time']
        email = data['email']
        phone = data['phone']

        if username and lotname and address and total_number and remaining_number and price_info and start_time_str and close_time_str and email and phone and password and password2:
            if (not ParkingLot.objects.all().filter(username=username)) and (not ParkingLot.objects.all().filter(lotname=lotname)):
                if pwd_validate(password, password2):
                    _a = time.strptime(start_time_str, '%H:%M')
                    _b = time.strptime(close_time_str, '%H:%M')
                    start_time = datetime(*_a[:5])
                    close_time = datetime(*_b[:5])

                    
                    #login_validate(request, username, password)
                    try:
                        f = open('registration/master-public.pem')
                        response['public_key'] = f.read()
                    except:
                        response['status'] = '101' #file I/O error, fail to issue public key
                        return JsonResponse(response)
                    parking_lot = ParkingLot.objects.create(username=username, password=password, lotname=lotname, address=address, 
                                 total_number=total_number, remaining_number=remaining_number, price_info=price_info, 
                                 start_time=start_time, close_time=close_time, email=email, phone=phone)
                    parking_lot.save()
                    response['status'] = '11'
                else:
                    response['status'] = '011' # password doesn't match
            else:
                #The username has existed, please change your username
                response['status'] = '00' # username exists
        else:
            # information missing
            response['status'] = '01'
    else:
        response['status'] = '10'
    return JsonResponse(response)

@csrf_exempt
def mylogin_operator(request):
    status=str()
    response = {}
    if request.method == 'POST':
        try:
            data = json.loads(request.body)
        except:
            response['status'] = '10'
            return JsonResponse(response)
        username = data['username']
        password = data['password']
            #user = authenticate(request, username=usr_name, password=passwd)
        if username and password:
            result = login_validate(request, username, password, ParkingLot)
            if result == 0:
                response['status'] = '11'
            else:
                response['status'] = '001'
        else:
            response['status'] = '00'
    return JsonResponse(response)

@csrf_exempt
def changepassword_operator(request):
    return

