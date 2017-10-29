/* **DISCLAIMER**
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * Without limitation of the foregoing, Contributors/Regents expressly does not warrant that:
 * 1. the software will meet your requirements or expectations;
 * 2. the software or the software content will be free of bugs, errors, viruses or other defects;
 * 3. any results, output, or data provided through or generated by the software will be accurate, up-to-date, complete or reliable;
 * 4. the software will be compatible with third party software;
 * 5. any errors in the software will be corrected.
 * The user assumes all responsibility for selecting the software and for the results obtained from the use of the software. The user shall bear the entire risk as to the quality and the performance of the software.
 */ 
 
/**
 *  Smart Thermostat Scheduler
 *
 * Base code from mwoodengr@hotmail.com, bugfixed and enhanced by alpharesearch.
 *
 */
definition(
    name: "Smart Thermostat Scheduler",
    namespace: "alpharesearch",
    author: "Alpharesearch",
    description: "Weekday and Weekend Thermostat",
    category: "Green Living",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/GreenLiving/Cat-GreenLiving.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/GreenLiving/Cat-GreenLiving@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/GreenLiving/Cat-GreenLiving@3x.png")

preferences {
    section("Monitor the outside temperature...") {
        input "temperatureSensor1", "capability.temperatureMeasurement"
    }
    section("Change HVAC mode to heat or cool if outside temperature... inbetween is set to auto") {
        input "temperatureH", "number", title: "Temp Degrees Fahrenheit < Heat?", defaultValue: "50"
        input "temperatureC", "number", title: "Temp Degrees Fahrenheit > Cool?", defaultValue: "90"
    }
    section("Choose thermostat... ") {
        input "thermostat", "capability.thermostat"
    }
    section("Sunday Sleep, Monday thru Friday Return Schedule") {
        input ("timeWake", "time", title: "Wake Time of Day", defaultValue: "2015-01-09T05:00:00.000-0500")	
        input ("tempSetpointWakeHeat", "number", title: "Wake Heat Temp Degrees Fahrenheit?", defaultValue: "68")
        input ("tempSetpointWakeCool", "number", title: "Wake Cool Temp Degrees Fahrenheit?", defaultValue: "78")
        input ("timeLeave", "time", title: "Leave Time of Day", defaultValue: "2015-01-09T05:45:00.000-0500")
        input ("tempSetpointLeaveHeat", "number", title: "Leave Heat Temp Degrees Fahrenheit?", defaultValue: "70")
        input ("tempSetpointLeaveCool", "number", title: "Leave Cool Temp Degrees Fahrenheit?", defaultValue: "76")
        input ("timeReturn", "time", title: "Return Time of Day", defaultValue: "2015-01-09T06:00:00.000-0500")
        input ("tempSetpointReturnHeat", "number", title: "Return Heat Degrees Fahrenheit?", defaultValue: "72")
        input ("tempSetpointReturnCool", "number", title: "Return Cool Degrees Fahrenheit?", defaultValue: "74")
        input ("timeSleep", "time", title: "Sleep Time of Day", defaultValue: "2015-01-09T22:00:00.000-0500")
        input ("tempSetpointSleepHeat", "number", title: "Sleep Heat Degrees Fahrenheit?", defaultValue: "64")
        input ("tempSetpointSleepCool", "number", title: "Sleep Cool Degrees Fahrenheit?", defaultValue: "82")
    }
    section("Friday Sleep, Saturday (vacation mode locks in Saturday) and Sunday Return Schedule") {
        input ("timeWakeWE", "time", title: "Wake Time of Day", defaultValue: "2015-01-09T08:00:00.000-0500")	
        input ("tempSetpointWakeHeatWE", "number", title: "Wake Heat Temp Degrees Fahrenheit?", defaultValue: "68")
        input ("tempSetpointWakeCoolWE", "number", title: "Wake Cool Temp Degrees Fahrenheit?", defaultValue: "78")
        input ("timeLeaveWE", "time", title: "Leave Time of Day", defaultValue: "2015-01-09T09:00:00.000-0500")
        input ("tempSetpointLeaveHeatWE", "number", title: "Leave Heat Temp Degrees Fahrenheit?", defaultValue: "70")
        input ("tempSetpointLeaveCoolWE", "number", title: "Leave Cool Temp Degrees Fahrenheit?", defaultValue: "78")
        input ("timeReturnWE", "time", title: "Return Time of Day", defaultValue: "2015-01-09T10:00:00.000-0500")
        input ("tempSetpointReturnHeatWE", "number", title: "Return Heat Degrees Fahrenheit?", defaultValue: "72")
        input ("tempSetpointReturnCoolWE", "number", title: "Return Cool Degrees Fahrenheit?", defaultValue: "74")
        input ("timeSleepWE", "time", title: "Sleep Time of Day", defaultValue: "2015-01-09T23:30:00.000-0500")
        input ("tempSetpointSleepHeatWE", "number", title: "Sleep Heat Degrees Fahrenheit?", defaultValue: "64")
        input ("tempSetpointSleepCoolWE", "number", title: "Sleep Cool Degrees Fahrenheit?", defaultValue: "82")
    }
    section("Vacation Mode switch (everyday uses Saturday schedule)...") {
		input "switchVM", "capability.switch", required: false
	}
    section("Turn AC zone off at sleep and back on at return in heating mode...") {
		input "switchAC", "capability.switch", required: false
	}
    section("Extra AUX switch (turns switch on when the outside temp is <)...") {
		input "switchAUX", "capability.switch", required: false
        input "temperatureAUXOn", "number", title: "Temp Degrees Fahrenheit turn on <?", defaultValue: "20",required: false
        input "temperatureAUXOff", "number", title: "Temp Degrees Fahrenheit turn off >?", defaultValue: "30",required: false
	}
    section("When all of these people leave home") {
        input "people", "capability.presenceSensor", multiple: true, required: false
    }
    section("Change to this temperatures") {
        input ("tempSetpointGoneHeat", "number", title: "Gone Heat Degrees Fahrenheit?", defaultValue: "64")
        input ("tempSetpointGoneCool", "number", title: "Gone Cool Degrees Fahrenheit?", defaultValue: "82")
    }
    section("False alarm threshold (defaults to 10 min)") {
        input "falseAlarmThreshold", "decimal", title: "Number of minutes", defaultValue: "10"
    }
}

def installed()
{
    setup()
    initialize()
}

def updated()
{
    unsubscribe()
    setup()
    initialize()
}

def setup()
{
    log.debug "Updated with settings: ${settings}"
    log.debug "Current mode = ${location.mode}, people = ${people.collect{it.label + ': ' + it.currentPresence}}"
    
    subscribe(temperatureSensor1, "temperature", temperatureHandler)
    subscribe(thermostat, "thermostat", thermostatHandler)
    subscribe(thermostat, "tempSetpointWakeHeat", HeatingSetpoint1Handler)
    subscribe(thermostat, "tempSetpointLeaveHeat", HeatingSetpoint2Handler)
    subscribe(thermostat, "tempSetpointReturnHeat", HeatingSetpoint3Handler)
    subscribe(thermostat, "tempSetpointSleepHeat", HeatingSetpoint4Handler)
    subscribe(thermostat, "tempSetpointGoneHeat", HeatingSetpoint5Handler)

    subscribe(thermostat, "tempSetpointWakeHeatWE", HeatingSetpoint11Handler)
    subscribe(thermostat, "tempSetpointLeaveHeatWE", HeatingSetpoint21Handler)
    subscribe(thermostat, "tempSetpointReturnHeatWE", HeatingSetpoint31Handler)
    subscribe(thermostat, "tempSetpointSleepHeatWE", HeatingSetpoint41Handler)
 
    subscribe(thermostat, "tempSetpointWakeCool", CoolingSetpoint1Handler)
    subscribe(thermostat, "tempSetpointLeaveCool", CoolingSetpoint2Handler)
    subscribe(thermostat, "tempSetpointReturnCool", CoolingSetpoint3Handler)
    subscribe(thermostat, "tempSetpointSleepCool", CoolingSetpoint4Handler)
    subscribe(thermostat, "tempSetpointGoneCool", CoolingSetpoint5Handler)

    subscribe(thermostat, "tempSetpointWakeCoolWE", CoolingSetpointA1Handler)
    subscribe(thermostat, "tempSetpointLeaveCoolWE", CoolingSetpointA2Handler)
    subscribe(thermostat, "tempSetpointReturnCoolWE", CoolingSetpointA3Handler)
    subscribe(thermostat, "tempSetpointSleepCoolWE", CoolingSetpointA4Handler)
    
    subscribe(people, "presence", presence)
}
def modeChangeHandler(evt) {
	log.debug "Reinitializing thermostats on new mode $evt.value"
    initialize()
}
// This section sets the HVAC mode based outside temperature. HVAC fan mode is set to "auto".
def temperatureHandler(evt) {
    def lastTemp = temperatureSensor1.currentTemperature
    def thermostatState = thermostat.currentthermostatMode
    def thermostatFan = thermostat.currentthermostatFanMode
    log.debug "lastTemp: $lastTemp thermostatState:$thermostatState thermostatFan:$thermostatFan"

	if (lastTemp <= temperatureH) {
    	def hvacmode = "heat"
        log.debug "HVAC mode set to $hvacmode"
        thermostat.setThermostatMode(hvacmode)
        }
    else if (lastTemp <= temperatureC) {
  		def hvacmode = "auto"
        log.debug "HVAC mode set to $hvacmode"
        thermostat.setThermostatMode(hvacmode)
    }
    else {
    def hvacmode = "cool"
        log.debug "HVAC mode set to $hvacmode"
        thermostat.setThermostatMode(hvacmode)
    }
    
    if (thermostatFan != "auto"){
    	//thermostat.setThermostatFanMode("auto")
    	//log.debug "HVAC fan mode set to auto"
    }
    
    if(switchAUX != null){
    	def currentState = switchAUX.currentValue("switch")
    	log.debug("switch for AUX is $currentState")
        if (lastTemp <= temperatureAUXOn) {
    		switchAUX.on()
       	 	log.debug "set AUX to ON"
        } else if (lastTemp >= temperatureAUXOff) {
        	switchAUX.off()
       	 	log.debug "set AUX to OFF"
        }
    }
}

// This section determines which day it is.
def initialize() {
	
    def calendar = Calendar.getInstance()
    calendar.setTimeZone(TimeZone.getTimeZone("America/New_York"))
    def today = calendar.get(Calendar.DAY_OF_WEEK)
    def todayValid = null
    switch (today) {
    case Calendar.MONDAY:
        todayValid = days.find{it.equals("Monday")}
        today = "Monday"
        break
    case Calendar.TUESDAY:
        todayValid = days.find{it.equals("Tuesday")}
        today = "Tuesday"
        break
    case Calendar.WEDNESDAY:
        todayValid = days.find{it.equals("Wednesday")}
        today = "Wednesday"
        break
    case Calendar.THURSDAY:
        todayValid = days.find{it.equals("Thursday")}
        today = "Thursday"
        break
    case Calendar.FRIDAY:
        todayValid = days.find{it.equals("Friday")}
        today = "Friday"
        break
    case Calendar.SATURDAY:
        todayValid = days.find{it.equals("Saturday")}
        today = "Saturday"
        break
    case Calendar.SUNDAY:
        todayValid = days.find{it.equals("Sunday")}
        today = "Sunday"
        break
    }
    log.debug("The day is $today")
    
	if(switchVM != null){
    	def currentState = switchVM.currentValue("switch")
		if (currentState == "on") {
    		log.debug("vacation mode $currentState today is now Saturday")
        	today = "Saturday"
    	}
    	if (currentState == "off") {
    		log.debug("vacation mode $currentState")
    	}
    }
    
    unschedule()
    
    schedule("2015-01-09T00:00:00.000-0500", "initialize")

    // This section is where the time/temperature schedule is set.
   if (today == "Monday") {
    	if (timeOfDayIsBetween("2015-01-09T00:00:00.000-0500", timeWake, new Date(), location.timeZone)) {
     		log.debug("0:00 to wake")
            changetempWeekSleep()
    	} 
    	if (timeOfDayIsBetween(timeWake, timeLeave, new Date(), location.timeZone)) {
     		log.debug("wake to leave")
            changetempWeekWake()
    	}
        if (timeOfDayIsBetween(timeLeave, timeReturn, new Date(), location.timeZone)) {
     		log.debug("leave to return")
            changetempWeekLeave()
    	}
        if (timeOfDayIsBetween(timeReturn, timeSleep, new Date(), location.timeZone)) {
     		log.debug("return to sleep")
            changetempWeekReturn()
    	}
        if (timeOfDayIsBetween(timeSleep, "2015-01-09T23:59:59.000-0500", new Date(), location.timeZone)) {
     		log.debug("sleep to 23:50")
            changetempWeekSleep()
    	}    
        schedule(timeWake, changetempWeekWake)
        schedule(timeLeave, changetempWeekLeave)
        schedule(timeReturn, changetempWeekReturn)
        schedule(timeSleep, changetempWeekSleep)
    }
   if (today =="Tuesday") {
    if (timeOfDayIsBetween("2015-01-09T00:00:00.000-0500", timeWake, new Date(), location.timeZone)) {
     		log.debug("0:00 to wake")
            changetempWeekSleep()
    	} 
    	if (timeOfDayIsBetween(timeWake, timeLeave, new Date(), location.timeZone)) {
     		log.debug("wake to leave")
            changetempWeekWake()
    	}
        if (timeOfDayIsBetween(timeLeave, timeReturn, new Date(), location.timeZone)) {
     		log.debug("leave to return")
            changetempWeekLeave()
    	}
        if (timeOfDayIsBetween(timeReturn, timeSleep, new Date(), location.timeZone)) {
     		log.debug("return to sleep")
            changetempWeekReturn()
    	}
        if (timeOfDayIsBetween(timeSleep, "2015-01-09T23:59:59.000-0500", new Date(), location.timeZone)) {
     		log.debug("sleep to 23:50")
            changetempWeekSleep()
    	}
    	schedule(timeWake, changetempWeekWake)
        schedule(timeLeave, changetempWeekLeave)
        schedule(timeReturn, changetempWeekReturn)
        schedule(timeSleep, changetempWeekSleep)
    }
   if (today =="Wednesday") {
    if (timeOfDayIsBetween("2015-01-09T00:00:00.000-0500", timeWake, new Date(), location.timeZone)) {
     		log.debug("0:00 to wake")
            changetempWeekSleep()
    	} 
    	if (timeOfDayIsBetween(timeWake, timeLeave, new Date(), location.timeZone)) {
     		log.debug("wake to leave")
            changetempWeekWake()
    	}
        if (timeOfDayIsBetween(timeLeave, timeReturn, new Date(), location.timeZone)) {
     		log.debug("leave to return")
            changetempWeekLeave()
    	}
        if (timeOfDayIsBetween(timeReturn, timeSleep, new Date(), location.timeZone)) {
     		log.debug("return to sleep")
            changetempWeekReturn()
    	}
        if (timeOfDayIsBetween(timeSleep, "2015-01-09T23:59:59.000-0500", new Date(), location.timeZone)) {
     		log.debug("sleep to 23:50")
            changetempWeekSleep()
    	}
    	schedule(timeWake, changetempWeekWake)
        schedule(timeLeave, changetempWeekLeave)
        schedule(timeReturn, changetempWeekReturn)
        schedule(timeSleep, changetempWeekSleep)
    }
   if (today =="Thrusday") {
    if (timeOfDayIsBetween("2015-01-09T00:00:00.000-0500", timeWake, new Date(), location.timeZone)) {
     		log.debug("0:00 to wake")
            changetempWeekSleep()
    	} 
    	if (timeOfDayIsBetween(timeWake, timeLeave, new Date(), location.timeZone)) {
     		log.debug("wake to leave")
            changetempWeekWake()
    	}
        if (timeOfDayIsBetween(timeLeave, timeReturn, new Date(), location.timeZone)) {
     		log.debug("leave to return")
            changetempWeekLeave()
    	}
        if (timeOfDayIsBetween(timeReturn, timeSleep, new Date(), location.timeZone)) {
     		log.debug("return to sleep")
            changetempWeekReturn()
    	}
        if (timeOfDayIsBetween(timeSleep, "2015-01-09T23:59:59.000-0500", new Date(), location.timeZone)) {
     		log.debug("sleep to 23:50")
            changetempWeekSleep()
    	}
    	schedule(timeWake, changetempWeekWake)
        schedule(timeLeave, changetempWeekLeave)
        schedule(timeReturn, changetempWeekReturn)
        schedule(timeSleep, changetempWeekSleep)
    }
   if (today =="Friday") {
    if (timeOfDayIsBetween("2015-01-09T00:00:00.000-0500", timeWake, new Date(), location.timeZone)) {
     		log.debug("0:00 to wake")
            changetempWeekSleep()
    	} 
    	if (timeOfDayIsBetween(timeWake, timeLeave, new Date(), location.timeZone)) {
     		log.debug("wake to leave")
            changetempWeekWake()
    	}
        if (timeOfDayIsBetween(timeLeave, timeReturn, new Date(), location.timeZone)) {
     		log.debug("leave to return")
            changetempWeekLeave()
    	}
        if (timeOfDayIsBetween(timeReturn, timeSleep, new Date(), location.timeZone)) {
     		log.debug("return to sleep")
            changetempWeekReturn()
    	}
        if (timeOfDayIsBetween(timeSleepWE, "2015-01-09T23:59:59.000-0500", new Date(), location.timeZone)) {
     		log.debug("sleep to 23:50")
            changetempWeekEndSleep()
    	}
    	schedule(timeWake, changetempWeekWake)
        schedule(timeLeave, changetempWeekLeave)
        schedule(timeReturn, changetempWeekReturn)
        schedule(timeSleepWE, changetempWeekEndSleep)
    }
   if (today =="Saturday") {
    	if (timeOfDayIsBetween("2015-01-09T00:00:00.000-0500", timeWakeWE, new Date(), location.timeZone)) {
     		log.debug("0:00 to wake")
            changetempWeekEndSleep()
    	} 
    	if (timeOfDayIsBetween(timeWakeWE, timeLeaveWE, new Date(), location.timeZone)) {
     		log.debug("wake to leave")
            changetempWeekEndWake()
    	}
        if (timeOfDayIsBetween(timeLeaveWE, timeReturnWE, new Date(), location.timeZone)) {
     		log.debug("leave to return")
            changetempWeekEndLeave()
    	}
        if (timeOfDayIsBetween(timeReturnWE, timeSleepWE, new Date(), location.timeZone)) {
     		log.debug("return to sleep")
            changetempWeekEndReturn()
    	}
        if (timeOfDayIsBetween(timeSleepWE, "2015-01-09T23:59:59.000-0500", new Date(), location.timeZone)) {
     		log.debug("sleep to 23:50")
            changetempWeekEndSleep()
    	}
        schedule(timeWakeWE, changetempWeekEndWake)
        schedule(timeLeaveWE, changetempWeekEndLeave)
        schedule(timeReturnWE, changetempWeekEndReturn)
        schedule(timeSleepWE, changetempWeekEndSleep)
    }
   if (today =="Sunday") {
    if (timeOfDayIsBetween("2015-01-09T00:00:00.000-0500", timeWakeWE, new Date(), location.timeZone)) {
     		log.debug("0:00 to wake")
            changetempWeekEndSleep()
    	} 
    	if (timeOfDayIsBetween(timeWakeWE, timeLeaveWE, new Date(), location.timeZone)) {
     		log.debug("wake to leave")
            changetempWeekEndWake()
    	}
        if (timeOfDayIsBetween(timeLeaveWE, timeReturnWE, new Date(), location.timeZone)) {
     		log.debug("leave to return")
            changetempWeekEndLeave()
    	}
        if (timeOfDayIsBetween(timeReturnWE, timeSleepWE, new Date(), location.timeZone)) {
     		log.debug("return to sleep")
            changetempWeekEndReturn()
    	}
        if (timeOfDayIsBetween(timeSleep, "2015-01-09T23:59:59.000-0500", new Date(), location.timeZone)) {
     		log.debug("sleep to 23:50")
            changetempWeekSleep()
    	}
    	schedule(timeWakeWE, changetempWeekEndWake)
        schedule(timeLeaveWE, changetempWeekEndLeave)
        schedule(timeReturnWE, changetempWeekEndReturn)
        schedule(timeSleep, changetempWeekSleep)
    }
}

// This section is where the thermostat temperature settings are set. 
def changetempWeekWake() {
    def thermostatState = thermostat.currentthermostatMode
    log.debug "checking mode request = $thermostatState"
    if (thermostatState == "auto"){
		thermostat.setHeatingSetpoint(tempSetpointWakeHeat)
    	thermostat.setCoolingSetpoint(tempSetpointWakeCool)
    }
    else if (thermostatState == "heat"){
		thermostat.setHeatingSetpoint(tempSetpointWakeHeat)
    }
    else {
		thermostat.setCoolingSetpoint(tempSetpointWakeCool)
    }
    log.debug "updating setpoints"
}
def changetempWeekLeave() {
    def thermostatState = thermostat.currentthermostatMode
    log.debug "checking mode request = $thermostatState"
    if (thermostatState == "auto"){
		thermostat.setHeatingSetpoint(tempSetpointLeaveHeat)
    	thermostat.setCoolingSetpoint(tempSetpointLeaveCool)
    }
    else if (thermostatState == "heat"){
		thermostat.setHeatingSetpoint(tempSetpointLeaveHeat)
    }
    else {
		thermostat.setCoolingSetpoint(tempSetpointLeaveCool)
    }
    log.debug "updating setpoints"
}
def changetempWeekReturn() {
    def thermostatState = thermostat.currentthermostatMode
    log.debug "checking mode request = $thermostatState"
    if (thermostatState == "auto"){
		thermostat.setHeatingSetpoint(tempSetpointReturnHeat)
    	thermostat.setCoolingSetpoint(tempSetpointReturnCool)
    }
    else if (thermostatState == "heat"){
		thermostat.setHeatingSetpoint(tempSetpointReturnHeat)
        if(switchAC != null) {
    		log.debug "switchAC zone On"
    		switchAC.on()
    	}
    }
    else {
		thermostat.setCoolingSetpoint(tempSetpointReturnCool)
    }
    log.debug "updating setpoints"
}
def changetempWeekSleep() {
    def thermostatState = thermostat.currentthermostatMode
    log.debug "checking mode request = $thermostatState"
    if (thermostatState == "auto"){
		thermostat.setHeatingSetpoint(tempSetpointSleepHeat)
    	thermostat.setCoolingSetpoint(tempSetpointSleepCool)
    }
    else if (thermostatState == "heat"){
		thermostat.setHeatingSetpoint(tempSetpointSleepHeat)
        if(switchAC != null) {
    		log.debug "switchAC zone Off"
    		switchAC.off()
    	}
    }
    else {
		thermostat.setCoolingSetpoint(tempSetpointSleepCool)
    }
    log.debug "updating setpoints"
}

def changetempWeekEndWake() {
    def thermostatState = thermostat.currentthermostatMode
    log.debug "checking mode request = $thermostatState"
    if (thermostatState == "auto"){
		thermostat.setHeatingSetpoint(tempSetpointWakeHeatWE)
    	thermostat.setCoolingSetpoint(tempSetpointWakeCoolWE)
    }
    else if (thermostatState == "heat"){
		thermostat.setHeatingSetpoint(tempSetpointWakeHeatWE)
    }
    else {
		thermostat.setCoolingSetpoint(tempSetpointWakeCoolWE)
    }
    log.debug "updating setpoints"
}
def changetempWeekEndLeave() {
    def thermostatState = thermostat.currentthermostatMode
    log.debug "checking mode request = $thermostatState"
    if (thermostatState == "auto"){
		thermostat.setHeatingSetpoint(tempSetpointLeaveHeatWE)
    	thermostat.setCoolingSetpoint(tempSetpointLeaveCoolWE)
    }
    else if (thermostatState == "heat"){
		thermostat.setHeatingSetpoint(tempSetpointLeaveHeatWE)
    }
    else {
		thermostat.setCoolingSetpoint(tempSetpointLeaveCoolWE)
    }
    log.debug "updating setpoints"
}
def changetempWeekEndReturn() {
    def thermostatState = thermostat.currentthermostatMode
    log.debug "checking mode request = $thermostatState"
    if (thermostatState == "auto"){
		thermostat.setHeatingSetpoint(tempSetpointReturnHeatWE)
    	thermostat.setCoolingSetpoint(tempSetpointReturnCoolWE)
    }
    else if (thermostatState == "heat"){
		thermostat.setHeatingSetpoint(tempSetpointReturnHeatWE)
        if(switchAC != null) {
    		log.debug "switchAC zone On"
    		switchAC.on()
    	}
    }
    else {
		thermostat.setCoolingSetpoint(tempSetpointReturnCoolWE)
    }
    log.debug "updating setpoints"
}
def changetempWeekEndSleep() {
    def thermostatState = thermostat.currentthermostatMode
    log.debug "checking mode request = $thermostatState"
	if (thermostatState == "auto"){
		thermostat.setHeatingSetpoint(tempSetpointSleepHeatWE)
    	thermostat.setCoolingSetpoint(tempSetpointSleepCoolWE)
    }
    else if (thermostatState == "heat"){
		thermostat.setHeatingSetpoint(tempSetpointSleepHeatWE)
        if(switchAC != null) {
    		log.debug "switchAC zone Off"
    		switchAC.off()
    	}
    }
    else {
		thermostat.setCoolingSetpoint(tempSetpointSleepCoolWE)
    }
    log.debug "updating setpoints"
}
def changetempGone() {
    def thermostatState = thermostat.currentthermostatMode
    log.debug "checking mode request = $thermostatState"
	if (thermostatState == "auto"){
		thermostat.setHeatingSetpoint(tempSetpointGoneHeat)
    	thermostat.setCoolingSetpoint(tempSetpointGoneCool)
    }
    else if (thermostatState == "heat"){
		thermostat.setHeatingSetpoint(tempSetpointGoneHeat)
    }
    else {
		thermostat.setCoolingSetpoint(tempSetpointGoneCool)
    }
    log.debug "updating setpoints"
}

def presence(evt) {
    log.debug "evt.name: $evt.value"
    if (evt.value == "not present") {
    	log.debug "checking if everyone is away"
        if (everyoneIsAway()) {
            log.debug "starting sequence"
            runIn(findFalseAlarmThreshold() * 60, "takeAction", [overwrite: false])
        }
    }
    else {
        if (someoneIsBack()) {
        log.debug "someone is back, turn schedule back on"
        initialize()
        }
    }
}

// returns true if all configured sensors are not present,
// false otherwise.
private everyoneIsAway() {
    def result = true
    // iterate over our people variable that we defined
    // in the preferences method
    for (person in people) {
        if (person.currentPresence == "present") {
            // someone is present, so set our our result
            // variable to false and terminate the loop.
            result = false
            break
        }
    }
    log.debug "everyoneIsAway: $result"
    return result
}

private someoneIsBack() {
    def result = false
    for (person in people) {
        if (person.currentPresence == "present") {
            result = true
            break
        }
    }
    log.debug "everyoneIsAway: $result"
    return result
}

// gets the false alarm threshold, in minutes. Defaults to
// 10 minutes if the preference is not defined.
private findFalseAlarmThreshold() {
    // In Groovy, the return statement is implied, and not required.
    // We check to see if the variable we set in the preferences
    // is defined and non-empty, and if it is, return it.  Otherwise,
    // return our default value of 10
    (falseAlarmThreshold != null && falseAlarmThreshold != "") ? falseAlarmThreshold : 10
}

def takeAction() {
    if (everyoneIsAway()) {
        def threshold = 1000 * 60 * findFalseAlarmThreshold() - 1000
        def awayLongEnough = people.findAll { person ->
            def presenceState = person.currentState("presence")
            def elapsed = now() - presenceState.rawDateCreated.time
            elapsed >= threshold
        }
        log.debug "Found ${awayLongEnough.size()} out of ${people.size()} person(s) who were away long enough"
        if (awayLongEnough.size() == people.size()) {
            //def message = "${app.label} changed your mode to '${newMode}' because everyone left home"
            def message = "SmartThings changed your mode to '${newMode}' because everyone left home"
            log.info message
            changetempGone()
        } else {
            log.debug "not everyone has been away long enough; doing nothing"
        }
    } else {
        log.debug "not everyone is away; doing nothing"
    }
}

