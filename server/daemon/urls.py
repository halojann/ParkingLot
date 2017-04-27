from django.conf.urls import url
from . import views

app_name = 'daemon'

urlpatterns = [
    #url(r'^autocancel/$', views.autocancel),    
    url(r'^dailyreboot/$', views.dailyreboot),
]