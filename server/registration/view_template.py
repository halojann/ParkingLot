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
from models import Question, Choice, User
# Create your views here.


class RegisterForm(forms.Form):
    username = forms.CharField()
    email = forms.EmailField()
    password = forms.CharField(widget=forms.PasswordInput)
    password2 = forms.CharField(label='Confirm', widget=forms.PasswordInput)
    def pwd_validate(self, p1, p2):
        return p1 == p2
    
class LoginForm(forms.Form):
    username = forms.CharField()
    password = forms.CharField(widget=forms.PasswordInput)
    
class ChangepwdForm(forms.Form):
    username = forms.CharField()
    password = forms.CharField(label='Old password', widget=forms.PasswordInput)
    password1 = forms.CharField(label='New password', widget=forms.PasswordInput)
    password2 = forms.CharField(label='Confirm password', widget=forms.PasswordInput)

@csrf_exempt
def register(request):
    error=[]
    if request.method == 'POST':
        form = RegisterForm(request.POST)
        if form.is_valid():
            data = form.cleaned_data
            username = data['username']
            email = data['email']
            password = data['password']
            password2 = data['password2']
            if not User.objects.all().filter(username=username):
                if form.pwd_validate(password, password2):
                    user = User.objects.create(username=username, password=password, email=email)
                    user.save()
                    login_validate(request, username, password)
                    return render_to_response('registration/Welcome.html', {'user': username}, RequestContext(request))
                else:
                    error.append('Please input the same password')
            else:
                error.append('The username has existed, please change your username')
        else:
                error.append('Please input both username and password')
        #return HttpResponse('Registered Successfully!')
    
    else:
        form = RegisterForm()
    return render_to_response('registration/register.html', {'form': form, 'error': error}, RequestContext(request))

@csrf_exempt
def register_m(request):
    if request.method == 'POST':
        pass;
    return None;

def login_validate(request, user_name, pass_word):
    try:
        user = User.objects.get(username=user_name)
        if user.password == pass_word:
            #user = authenticate(username=user_name, password=pass_word)
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
def mylogin(request):
    error=[]
    if request.method == 'POST':
        form = LoginForm(request.POST)
        if form.is_valid():
            data = form.cleaned_data
            usrname = data['username']
            passwd = data['password']
            #user = authenticate(request, username=usr_name, password=passwd)
            result = login_validate(request, usrname, passwd)
            if result == 0:
                return render_to_response('registration/Welcome.html', {'user':usrname}, RequestContext(request))
            elif result == 2:
                error.append('Username does not exist')
            else:
                error.append('Please input the correct password')
                return HttpResponse(usrname+passwd)
                #return render_to_response('registration/registration/login.html', {'error': error, 'form': form}, RequestContext(request))
        else:
            error.append('Please input both username and password')
            #return HttpResponse('2')
            #return render_to_response('registration/registration/login.html', {'error': error, 'form': form}, RequestContext(request))
    else:
        form = LoginForm()
    return render_to_response('registration/login.html', {'error': error, 'form': form}, RequestContext(request))
@csrf_exempt
def mylogout(request):
    auth.logout(request)
    return HttpResponseRedirect('/accounts/login/')

@login_required
def index(request):
    latest_question_list = Question.objects.order_by('-pub_date')[:5]
    context = {'latest_question_list': latest_question_list}
    return render(request, 'registration/index.html', context)

@csrf_exempt
def changepassword(request, username):
    error = []
    if request.method == 'POST':
        form = ChangepwdForm(request.POST)
        if form.is_valid():
            data = form.cleaned_data
            user = authenticate(username=username,password=data['old_pwd'])
            if user is not None:
                if data['new_pwd']==data['new_pwd2']:
                    newuser = User.objects.get(username__exact=username)
                    newuser.set_password(data['new_pwd'])
                    newuser.save()
                    return HttpResponseRedirect('/login/')
                else:
                    error.append('Please input the same password')
            else:
                error.append('Please correct the old password')
        else:
            error.append('Please input the required domain')
    else:
        form = ChangepwdForm()
    return render_to_response('registration/changepassword.html',{'form':form,'error':error}, RequestContext(request))


"""
def login(request):
    template_name = 'registration/account/1.html'
    context = {'login_err': 'Login Error!'}
    return der(request, template_name)
    #return HttpResponse('you need to login first')
"""    
"""
class IndexView(generic.ListView):
    template_name = 'registration/index.html'
    context_object_name = 'latest_question_list'
    #@login_required
    def get_queryset(self):
        return Question.objects.filter(pub_date__lte=timezone.now()).order_by('-pub_date')[:5]
    #return render(request, 'registration/index.html', context)
    #return HttpResponse(template.render(context, request))
"""

@login_required
def detail(request, question_id):
    question = get_object_or_404(Question, pk=question_id)
    return render(request, 'registration/detail.html', {'question': question})

@login_required
def results(request, question_id):
    question = get_object_or_404(Question, pk=question_id)
    return render(request, 'registration/results.html', {'question': question})

@login_required
def vote(request, question_id):
    question = get_object_or_404(Question, pk=question_id)
    try:
        selected_choice = question.choice_set.get(pk=request.POST['choice'])
    except(KeyError, Choice.DoesNotExist):
        return render(request, 'registration/detail.html', {
            'question': question,
            'error_message': "You didn't select a choice.",
        })
    else:
        selected_choice.votes += 1
        selected_choice.save()
        return HttpResponseRedirect(reverse('registration:results', args=(question.id,)))


"""
#@login_required
def my_protected_view(request):
    #A view that can only be accessed by logged-in users
    return render(request, 'protected.html', {"current_user": request.user})
    """
    