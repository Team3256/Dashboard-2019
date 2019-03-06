#!/usr/bin/env python3
#
# This is a NetworkTables server (eg, the robot or simulator side).
#
# On a real robot, you probably would create an instance of the
# wpilib.SmartDashboard object and use that instead -- but it's really
# just a passthru to the underlying NetworkTable object.
#
# When running, this will continue incrementing the value 'robotTime',
# and the value should be visible to networktables clients such as
# SmartDashboard. To view using the SmartDashboard, you can launch it
# like so:
#
#     SmartDashboard.jar ip 127.0.0.1
#

import time
from networktables import NetworkTables

# To see messages from networktables, you must setup logging
import logging
logging.basicConfig(level=logging.DEBUG)

NetworkTables.initialize()
sd = NetworkTables.getTable("SmartDashboard")

sd.putBoolean('isHomed', True)
sd.putStringArray('AutoOptions', ['Test 1', 'Test 2'])
sd.putString('ChosenAuto', 'Test 1')
sd.putString('alliance', 'Red')

i = 0
while True:
    print('robotTime:', sd.getNumber('robotTime', 'N/A'))

    if (sd.getString('regional', '') == 'Del Mar Regional'):
        sd.putString('regional', 'Monterey Regional')
    else:
        sd.putString('regional', 'Del Mar Regional')
    
    if (sd.getString('alliance', 'Red') == 'Red'):
        sd.putString('alliance', '')
    elif (sd.getString('alliance', 'Red') == ''):
        sd.putString('alliance', 'Blue')
    else:
        sd.putString('alliance', 'Red')

    sd.putNumber('match', sd.getNumber('match', 0) + 1)

    print(sd.getString('regional', ''))
    
    time.sleep(2)
    i += 1