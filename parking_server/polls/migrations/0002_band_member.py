# -*- coding: utf-8 -*-
# Generated by Django 1.10.5 on 2017-04-03 03:52
from __future__ import unicode_literals

from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    dependencies = [
        ('polls', '0001_initial'),
    ]

    operations = [
        migrations.CreateModel(
            name='Band',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=200)),
                ('can_rock', models.BooleanField(default=True)),
            ],
        ),
        migrations.CreateModel(
            name='Member',
            fields=[
                ('id', models.AutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=200, verbose_name="Member's name")),
                ('instrument', models.CharField(choices=[('g', 'Guitar'), ('b', 'Bass'), ('d', 'Drums')], max_length=1)),
                ('band', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='polls.Band')),
            ],
        ),
    ]
