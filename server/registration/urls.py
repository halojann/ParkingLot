from django.conf.urls import url
from . import views

app_name = 'registration'

urlpatterns = [
    #url(r'^account/login/$', views.login, name='login'),
    #url(r'^account/$', views.login, name='login'),
    #ex: /polls/
    #url(r'^$', views.index, name='index'),
    #ex: /polls/2/
    #url(r'(?P<question_id>[0-9]+)/$', views.detail, name='detail'),
    #ex: /polls/2/results/
    #url(r'^(?P<question_id>[0-9]+)/results/$', views.results, name='results'),
    #ex: /polls/2/vote/
    #url(r'^(?P<question_id>[0-9]+)/vote/$', views.vote, name='vote'),
    url(r'^hello/$', views.hello),
    
    url(r'^login_user/$', views.mylogin_user),
    url(r'^register_user/$', views.register_user),
    url(r'^login/register_user/$', views.register_user),
    url(r'^changepassword_user/(?P<username>\w+)/$', views.changepassword_user),
    
    url(r'^login_operator/$', views.mylogin_operator),
    url(r'^register_operator/$', views.register_operator),
    url(r'^login/register_operator/$', views.register_operator),
    url(r'^changepassword_operator/(?P<username>\w+)/$', views.changepassword_operator),
    #url(r'^profile/$', views.login),
    #url(r'^login/$', views.login, {'template_name': 'polls/registration/login.html'}, name='login'),
]