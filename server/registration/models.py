
from __future__ import unicode_literals
import datetime
from django.db import models
from django.utils import timezone
from django.utils.encoding import python_2_unicode_compatible
from django.contrib.auth.models import AbstractBaseUser, UserManager

# Create your models here.

@python_2_unicode_compatible
class User(models.Model):
    uid = models.AutoField(primary_key=True)
    username = models.CharField("UserName", max_length=20, unique=True)
    password = models.CharField(max_length=20)
    email = models.CharField(max_length=50)
    phone = models.CharField(max_length=20)
    def __str__(self):
        return self.username
    
    
@python_2_unicode_compatible
class ParkingLot(models.Model):
    pid = models.AutoField(primary_key=True)
    username = models.CharField(max_length=20, unique=True)
    password = models.CharField(max_length=20)
    lotname = models.CharField("ParkingLot Name", max_length=50, unique=True)
    address = models.CharField("ParkingLot Address", max_length=100)
    total_number = models.IntegerField()
    remaining_number = models.CharField()
    price_info = models.CharField(max_length=300)
    start_time = models.DateTimeField("Business Time From")
    close_time = models.DateTimeField("Business Time To")
    email = models.CharField(max_length=50)
    phone = models.CharField(max_length=20)
    def __str__(self):
        return self.lotname

  