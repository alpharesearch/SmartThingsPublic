/**
 *  Roku Virtual Buttons
 *
 *  Copyright 2017 Sam Steele
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
    name: "Roku Virtual Buttons",
    namespace: "c99koder",
    author: "Sam Steele",
    description: "Creates virtual buttons to switch between apps / inputs on a Roku TV",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
    page(name: "page1", title: "Select Roku Device", nextPage: "page2", uninstall: true) {
    	section("Title") {
            input "roku","capability.mediaController", title: "Roku Device", multiple: false, required: true
        }
	}
    page(name: "page2", title: "Select Channels", install: true, uninstall: true)
}

def page2() {
    def switchChannels = ChannelsDiscovered()
    dynamicPage(name: "page2") {
        section("Channels") {
            input "selectedChannels", "enum", required:false, title:"Select Channels\n(${switchChannels.size() ?: 0} found)", multiple:true, options:switchChannels
        }
    }
}

def installed() {
	initialize()
}

def updated() {
	getAllChildDevices().each {
        	subscribe(it, "switch", switchHandler)
    }
	unsubscribe()
	initialize()
}

def initialize() {
	subscribe(roku, "activityList", createButtons)
    createButtons(null)
    roku.getAllActivities()
}

def createButtons(evt) {
    def activityList = roku.currentValue("activityList")
    if (activityList != null) {
        def appsNode = new XmlSlurper().parseText(activityList)

        appsNode.children().each{
            def appId = it.@id.toString()
            def deviceLabel = it.text()
            if (getChildDevice(appId) == null) {
                selectedChannels.each { id ->
                log.debug "Checking $id against $appId"
                	if(id == appId){
                		def device = addChildDevice("smartthings", "Momentary Button Tile", appId, null, [label: "Roku: $deviceLabel"])
                		state["$device.id"] = appId
                		log.debug "Created button tile $device.id for channel $deviceLabel ($appId)"
                    	}
                    }
            } else {
                log.debug "Skipped $appId"
            }
        }
	}
    
    if (getChildDevice("powerOn") == null) {
        def device = addChildDevice("smartthings", "Momentary Button Tile", "powerOn", null, [label: "Roku: Power On"])
        state["$device.id"] = "powerOn"
        log.debug "Created Power On tile $device.id"
    } else {
        log.debug "Skipped Power On tile"
    }

    if (getChildDevice("powerOff") == null) {
        def device = addChildDevice("smartthings", "Momentary Button Tile", "powerOff", null, [label: "Roku: Power Off"])
        state["$device.id"] = "powerOff"
        log.debug "Created Power Off tile $device.id"
    } else {
        log.debug "Skipped Power Off tile"
    }

	getAllChildDevices().each {
        	subscribe(it, "switch", switchHandler)
    }
}

def switchHandler(evt) {
	def state_evn = state["$evt.device.id"]
	log.debug "switchHandler: $state_evn $evt.device.id"
    if (evt.value == "on") {
    	if(state["$evt.device.id"] == "powerOn") {
            sendHubCommand(new physicalgraph.device.HubAction ("wake on lan ${roku.deviceNetworkId}", physicalgraph.device.Protocol.LAN, null, [:]))
            roku.pressKey("Power")
        } else if(state["$evt.device.id"] == "powerOff") {
            roku.pressKey("PowerOff")
        } else {
	    	roku.launchAppId(state["$evt.device.id"])
        }
    }
}

Map ChannelsDiscovered() {
	roku.getAllActivities()
    def devicemap = [:]
	def activityList = roku.currentValue("activityList")
    if (activityList != null) {
        def appsNode = new XmlSlurper().parseText(activityList)
        appsNode.children().each{
            def appId = it.@id.toString()
            def deviceLabel = it.text()
            log.debug "ChannelsDiscovered: $deviceLabel ($appId)"
			def value = deviceLabel
			def key =  appId
			devicemap["${key}"] = value
        }
	}
	return devicemap
}