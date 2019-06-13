# Movisens Bluetooth Example Project

The following project is made to showcase the connection between a smartphone and a movisens sensors via Bluetooth. It implements the dataflows to connect to a movisens sensor and stay connected over a long time even with dropouts.

The project contains a way to find movisens sensors nearby and to connect to them. It uses the in chapter [Connection Data Flows](#dataflows) described data flows.

A Android foreground service will run the sensing in the background and is capable to reconnect, when a sensor will disconnect. In order to get a safe reconnect we have to implement the reconnect logic via a [setAlarmClock](https://developer.android.com/reference/android/app/AlarmManager#setAlarmClock(android.app.AlarmManager.AlarmClockInfo,%2520android.app.PendingIntent)) mechanism. This is necessary to get a reconnect in time even when the smartphone has a newer Android with version 7 or higher and uses doze mode. 

To read more about doze mode restrictions see [here](https://developer.android.com/training/monitoring-device-state/doze-standby).

## How to connect to a movisens Sensor

This chapter will describe which requirements you have to meet and which data flows are necessary to follow in order to connect to a movisens sensor and hold your connection without issues.

### Prerequisites
- movisens ECG, EDA or Move Sensor
    - Generation 3 or higher
- At least firmware version 1.12.0

### Connection Data Flows <a name="dataflows"></a>

Because of the high optimization of our sensors for mobile research there are a few data flows you have to implement in order to get a stable, non-faulty connection with no data loss.

#### First Connection

![alt text](https://docs.movisens.com/bluetooth-example/firstStart.png "First Connection Graph")

 >**WARNING**: Be careful when delete all data. If the data wasn't downloaded via the [SensorManager](https://www.movisens.com/en/sensormanager/), it results in unrecoverable data loss. You should never continue a measurement which is currently running via a fresh first start. If you activate different characteristics via indication than before, you won't receive any data.

#### Restart Connection (After an app or smartphone restart)

![alt text](https://docs.movisens.com/bluetooth-example/Restart.png "First Connection Graph")

 >**WARNING**: Be careful to never restart a connection after measurement enabled returns **false** and data is available returns **true**. It will never return data anymore.

## Used Technology 

The sample project is built on top of the following technologies:

- [Kotlin](https://kotlinlang.org/)
- [RxJava 2](https://github.com/ReactiveX/RxJava)
    - [RxBleAndroid](http://polidea.github.io/RxAndroidBle/)
- [Koin Dependency Injection](https://github.com/InsertKoinIO/koin)
- [Android ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)
- [Movisens Gattlib](https://github.com/movisens/MovisensGattLib) & [Smart Gattlib](https://github.com/movisens/SmartGattLib)

## Licence

---
    Copyright 2019 movisens GmbH

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

---

 