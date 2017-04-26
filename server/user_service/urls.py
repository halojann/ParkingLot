from django.conf.urls import url
from . import views

app_name = 'user_service'

urlpatterns = [
    #url(r'^$', views.index, name='index'),        
    url(r'^information/$', views.information),
    url(r'^reserve/$', views.reserve),
#     url(r'^arrive/$', views.arrive),
#     url(r'^leave/$', views.leave),
]