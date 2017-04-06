from django.contrib import admin
from .models import Question, Choice, Band, Member, User
# Register your models here.

class MemberAdmin(admin.ModelAdmin):
    """Customize the look of the auto-generated admin for the Member model
"""
    list_display = ('instrument', 'name')
    list_filter = ('band',)

admin.site.register(Question)
admin.site.register(Choice)
admin.site.register(Band) #use the default options
admin.site.register(Member, MemberAdmin) #use the customized options
admin.site.register(User)
# Register your models here.
