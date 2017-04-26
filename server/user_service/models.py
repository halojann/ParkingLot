from __future__ import unicode_literals
from django.db import models
from django.utils import timezone
from django.utils.encoding import python_2_unicode_compatible
import datetime
from registration.models import User, ParkingLot

# Create your models here.
@python_2_unicode_compatible
class Reserving(models.Model):
    transaction_no = models.AutoField(primary_key=True)
    uid = models.ForeignKey(User, on_delete=models.CASCADE)
    pid = models.CharField(ParkingLot, on_delete=models.CASCADE)
    start_time = models.DateTimeField("Reserving From")
    end_time = models.DateTimeField("Reserving To")    
    def __str__(self):
        return self.uid + "-" + self.pid