from django.contrib import admin
from .models import User, ParkingLot
import sys
sys.path.append('../')
from user_service.models import Transaction
# Register your models here.


admin.site.register(User)
admin.site.register(ParkingLot)
admin.site.register(Transaction)

