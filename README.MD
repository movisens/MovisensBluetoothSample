# Movisens Bluetooth Example Project

The following showcases the connection between a smartphone and movisens physiological sensors via Bluetooth. It implements the data flows connecting to a movisens sensor and remains connected over a long time even accounting for dropouts.

The project contains a way to find movisens sensors nearby and to connect to them. It uses the data flows described in the chapter [Connection data flows](#dataflows).

An Android foreground service runs the sensing in the background and reconnects when a sensor disconnects. In order to safely reconnect we implement the reconnect logic via a [setAlarmClock](https://developer.android.com/reference/android/app/AlarmManager#setAlarmClock(android.app.AlarmManager.AlarmClockInfo,%2520android.app.PendingIntent))  mechanism. This allows a prompt reconnect even when the smartphone operates on a newer Android phone with version 7 or higher and uses doze mode.

Read more about doze mode restrictions [here](https://developer.android.com/training/monitoring-device-state/doze-standby).

## How to connect to a movisens sensor

This chapter describes the necessary requirements and which data flows to follow in order to connect to a movisens sensor and hold your connection without issues.

### Prerequisites
- movisens ECG, EDA or Move sensor
    - Generation 3 or higher
- At least firmware version 1.12.0

### Connection data flows <a name="dataflows"></a>

The optimisation of our sensors for mobile research requires you to implement a few data flows in order to get a stable, non-faulty connection with no data loss.

#### First connection

![alt text](https://docs.movisens.com/bluetooth-example/firstStart.png "First Connection Graph")

 >**WARNING**: Be careful when deleting all data! If the data wasn't downloaded via the [SensorManager](https://www.movisens.com/en/sensormanager/), you permanently lose the recorded data. Never continue a measurement which is currently running via a fresh first start. If you activate different characteristics via indication than before, the sensor won’t transmit any data.

#### Restart connection (After an app or smartphone restart)

![alt text](https://docs.movisens.com/bluetooth-example/Restart.png "First Connection Graph")

 >**WARNING**: Be careful to never restart a connection after measurement enabled returns **false** and data is available returns  **true**. If this occurs, the sensor ceases data transmission.

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

 