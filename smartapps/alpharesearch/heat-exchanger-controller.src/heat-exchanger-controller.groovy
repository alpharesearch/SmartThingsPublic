/**
 *  Heat exchanger 
 *
 *  Copyright 2017 Markus Schulz
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "Heat Exchanger Controller",
    namespace: "Alpharesearch",
    author: "Markus Schulz",
    description: "Turns heat exchanger on or off depending on out side temperature. ",
    category: "Green Living",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Developers/whole-house-fan.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Developers/whole-house-fan.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Developers/whole-house-fan.png")

preferences {
	section("Monitor the temperature...") {
		input "temperatureSensor1", "capability.temperatureMeasurement"
	}
    section("When the temperature falles below...") {
		input "temperature1", "number", title: "Temperature?"
	}
	section("When the temperature rises above...") {
		input "temperature2", "number", title: "Temperature?"
	}
    section( "Notifications" ) {
        input("recipients", "contact", title: "Send notifications to") {
            input "sendPushMessage", "enum", title: "Send a push notification?", options: ["Yes", "No"], required: false
            input "phone1", "phone", title: "Send a Text Message?", required: false
        }
    }
	section("Turn on which A/C or fan...") {
		input "switch1", "capability.switch"
	}
    section("Activate/Deactive whole system...") {
		input "switchM", "capability.switch"
	}
}

def installed() {
	subscribe(temperatureSensor1, "temperature", temperatureHandler)
    subscribe(switchM, "switch.on", switchMOnHandler)
    subscribe(switchM, "switch.off", switchMOffHandler)
}

def updated() {
	unsubscribe()
	subscribe(temperatureSensor1, "temperature", temperatureHandler)
    subscribe(switchM, "switch.on", switchMOnHandler)
    subscribe(switchM, "switch.off", switchMOffHandler)
}

def switchMOnHandler(evt) {
	temperatureHandler(temperatureSensor1.currentState("temperature"))
}

def switchMOffHandler(evt) {
	switch1.off()
}

def temperatureHandler(evt) {
	log.trace "temperature: $evt.value, $evt"

	def tooCold = temperature1
    def tooHot = temperature2
	def mySwitch = settings.switch1
    
    def currentState = switchM.currentValue("switch")
    if (currentState == "on")
    {
    
        if (evt.doubleValue > tooCold && evt.doubleValue < tooHot) {
            log.debug "${temperatureSensor1.displayName} temperature of ${evt.value}${evt.unit?:tempScale} is OK, this falls between $tooCold and $tooHot so turning $mySwitch on"
            switch1.on()
        }

        if (evt.doubleValue <= tooCold) {
            log.debug "Checking how long the temperature sensor has been reporting <= $tooCold"

            // Don't send a continuous stream of text messages
            def deltaMinutes = 360 // TODO: Ask for "retry interval" in prefs?
            def timeAgo = new Date(now() - (1000 * 60 * deltaMinutes).toLong())
            def recentEvents = temperatureSensor1.eventsSince(timeAgo)?.findAll { it.name == "temperature" }
            log.trace "Found ${recentEvents?.size() ?: 0} events in the last $deltaMinutes minutes"
            def alreadySentSms = recentEvents.count { it.doubleValue >= tooHot } > 1

            if (alreadySentSms) {
                log.debug "SMS already sent within the last $deltaMinutes minutes"
                // TODO: Send "Temperature back to normal" SMS, turn switch off
            } else {
                log.debug "Temperature fell below $tooCold:  sending SMS and deactivating $mySwitch"
                def tempScale = location.temperatureScale ?: "F"
                send("${temperatureSensor1.displayName} is too cold, reporting a temperature of ${evt.value}${evt.unit?:tempScale}")
                switch1.off()
            }
        }
        // TODO: Replace event checks with internal state (the most reliable way to know if an SMS has been sent recently or not).
        if (evt.doubleValue >= tooHot) {
            log.debug "Checking how long the temperature sensor has been reporting <= $tooHot"

            // Don't send a continuous stream of text messages
            def deltaMinutes = 10 // TODO: Ask for "retry interval" in prefs?
            def timeAgo = new Date(now() - (1000 * 60 * deltaMinutes).toLong())
            def recentEvents = temperatureSensor1.eventsSince(timeAgo)?.findAll { it.name == "temperature" }
            log.trace "Found ${recentEvents?.size() ?: 0} events in the last $deltaMinutes minutes"
            def alreadySentSms = recentEvents.count { it.doubleValue >= tooHot } > 1

            if (alreadySentSms) {
                log.debug "SMS already sent within the last $deltaMinutes minutes"
                // TODO: Send "Temperature back to normal" SMS, turn switch off
            } else {
                log.debug "Temperature rose above $tooHot:  sending SMS and deactivating $mySwitch"
                def tempScale = location.temperatureScale ?: "F"
                send("${temperatureSensor1.displayName} is too hot, reporting a temperature of ${evt.value}${evt.unit?:tempScale}")
                switch1.off()
            }
        }
    }
}

private send(msg) {
    if (location.contactBookEnabled) {
        log.debug("sending notifications to: ${recipients?.size()}")
        if (sendPushMessage != "1") {
        	sendNotificationToContacts(msg, recipients)
        }
    }
    else {
        if (sendPushMessage == "Yes") {
            log.debug("sending push message $sendPushMessage")
            sendPush(msg)
        }

        if (phone1) {
            log.debug("sending text message")
            sendSms(phone1, msg)
        }
    }

    log.debug msg
}
