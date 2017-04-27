from __future__ import unicode_literals
from django.db import models
from django.utils import timezone
from django.utils.encoding import python_2_unicode_compatible
from datetime import datetime
import time
import sys
sys.path.append('../')
from registration.models import User, ParkingLot

_a = time.strptime('00:00', '%H:%M')
_b = time.strptime('00:00', '%H:%M')
arrive_default = datetime(*_a[:5])
leave_default = datetime(*_b[:5])

# Create your models here.
@python_2_unicode_compatible
class Transaction(models.Model):
    transaction_no = models.AutoField(primary_key=True)
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    lot = models.ForeignKey(ParkingLot, on_delete=models.CASCADE)
    start_time = models.DateTimeField("Reserve from")
    end_time = models.DateTimeField("Reserve to")
    arrive_time = models.DateTimeField("Arrive at", default=arrive_default)
    leave_time = models.DateTimeField("Leave at", default=leave_default)
    is_active = models.BooleanField("Transaction active?", default=True)  
    def __str__(self):
        return self.user.username + " ----> " + self.lot.lotname + " (" + str(self.transaction_no) + ")"