'''
Created on Apr 26, 2017

@author: Haotian Chi
'''
import urllib2
import json
import time
from threading import Timer

"""
def task1():
    data = {
        'username': 'chihaotian',
        'password': 'password'
        }
    url = 'http://127.0.0.1:8000/daemon/autocancel/'
    #url = 'http://google.com'
    headers = {'Content-Type': 'application/json'}
    request = urllib2.Request(url=url, headers=headers, data=json.dumps(data))
    try:
        response = urllib2.urlopen(request)
        print response
    except:
        print('request fails')
    global timer1
    timer1 = Timer(300, task1) # in fact, 300s
    timer1.start()
	"""

def task2():
    data = {
        'username': 'chihaotian',
        'password': 'password'
        }
    url = 'http://127.0.0.1:8000/daemon/dailyreboot/'
    #url = 'http://google.com'
    headers = {'Content-Type': 'application/json'}
    request = urllib2.Request(url=url, headers=headers, data=json.dumps(data))
    try:
        response = urllib2.urlopen(request)
        print response
    except:
        print('request fails')
    global timer2
    timer2 = Timer(3600, task2) # in fact, 3600s
    timer2.start()

task2()

time.sleep(1000000) # in fact, keep running
timer2.cancel()
