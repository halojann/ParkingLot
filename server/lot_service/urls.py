from django.conf.urls import url
from . import views

app_name = 'lot_service'

urlpatterns = [       
    url(r'^arrive/$', views.arrive),
    url(r'^leave/$', views.leave),
]