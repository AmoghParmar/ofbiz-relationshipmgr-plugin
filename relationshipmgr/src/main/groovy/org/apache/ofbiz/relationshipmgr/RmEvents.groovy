package org.apache.ofbiz.relationshipmgr

import org.apache.ofbiz.entity.GenericValue
import org.apache.ofbiz.base.util.UtilHttp
import org.apache.ofbiz.base.util.UtilDateTime

def createRmPerson() {
    Map parameters = UtilHttp.getParameterMap(request)
    String partyId = parameters.partyId
    if (!partyId) {
        partyId = delegator.getNextSeqId("RmParty")
    }
    try {
        GenericValue rmParty = delegator.findOne("RmParty", [partyId: partyId], false)
        if (!rmParty) {
            rmParty = delegator.makeValue("RmParty", [partyId: partyId, partyTypeId: "PERSON"])
            rmParty.create()
        }
        GenericValue rmPerson = delegator.makeValue("RmPerson", [partyId: partyId])
        rmPerson.setNonPKFields(parameters)
        rmPerson.create()
        return "success"
    } catch (Exception e) {
        request.setAttribute("_ERROR_MESSAGE_", e.getMessage())
        return "error"
    }
}

def createRmPartyRole() {
    Map parameters = UtilHttp.getParameterMap(request)
    GenericValue userLogin = (GenericValue) session.getAttribute("userLogin")
    String partyId = parameters.partyId
    String roleTypeId = parameters.roleTypeId
    if (!partyId || !roleTypeId) {
        request.setAttribute("_ERROR_MESSAGE_", "Missing partyId or roleTypeId")
        return "error"
    }
    try {
        GenericValue existingRole = delegator.findOne("RmPartyRole", [partyId: partyId, roleTypeId: roleTypeId], false)
        if (existingRole) {
            return "success"
        }
        dispatcher.runSync("createRmPartyRole", parameters + [userLogin: userLogin])
        return "success"
    } catch (Exception e) {
        request.setAttribute("_ERROR_MESSAGE_", e.getMessage())
        return "error"
    }
}

def deleteRmPartyRole() {
    Map parameters = UtilHttp.getParameterMap(request)
    GenericValue userLogin = (GenericValue) session.getAttribute("userLogin")
    try {
        dispatcher.runSync("deleteRmPartyRole", parameters + [userLogin: userLogin])
        return "success"
    } catch (Exception e) {
        request.setAttribute("_ERROR_MESSAGE_", e.getMessage())
        return "error"
    }
}

def createRmContactMechEmail() {
    Map parameters = UtilHttp.getParameterMap(request)
    GenericValue userLogin = (GenericValue) session.getAttribute("userLogin")
    try {
        Map contactMechResult = dispatcher.runSync("createRmContactMech", [contactMechTypeId: "EMAIL_ADDRESS", infoString: parameters.infoString, userLogin: userLogin])
        String contactMechId = contactMechResult.contactMechId
        dispatcher.runSync("createRmPartyContactMech", [partyId: parameters.partyId, contactMechId: contactMechId, fromDate: UtilDateTime.nowTimestamp(), userLogin: userLogin])
        return "success"
    } catch (Exception e) {
        request.setAttribute("_ERROR_MESSAGE_", e.getMessage())
        return "error"
    }
}

def createRmContactMechPhone() {
    Map parameters = UtilHttp.getParameterMap(request)
    GenericValue userLogin = (GenericValue) session.getAttribute("userLogin")
    try {
        Map contactMechResult = dispatcher.runSync("createRmContactMech", [contactMechTypeId: parameters.contactMechTypeId, infoString: parameters.infoString, userLogin: userLogin])
        String contactMechId = contactMechResult.contactMechId
        dispatcher.runSync("createRmPartyContactMech", [partyId: parameters.partyId, contactMechId: contactMechId, fromDate: UtilDateTime.nowTimestamp(), userLogin: userLogin])
        return "success"
    } catch (Exception e) {
        request.setAttribute("_ERROR_MESSAGE_", e.getMessage())
        return "error"
    }
}

def createRmPostalAddress() {
    Map parameters = UtilHttp.getParameterMap(request)
    GenericValue userLogin = (GenericValue) session.getAttribute("userLogin")
    try {
        Map contactMechResult = dispatcher.runSync("createRmContactMech", [contactMechTypeId: "POSTAL_ADDRESS", userLogin: userLogin])
        String contactMechId = contactMechResult.contactMechId
        dispatcher.runSync("createRmPostalAddress", [contactMechId: contactMechId, address1: parameters.address1, city: parameters.city, postalCode: parameters.postalCode, userLogin: userLogin])
        dispatcher.runSync("createRmPartyContactMech", [partyId: parameters.partyId, contactMechId: contactMechId, fromDate: UtilDateTime.nowTimestamp(), userLogin: userLogin])
        return "success"
    } catch (Exception e) {
        request.setAttribute("_ERROR_MESSAGE_", e.getMessage())
        return "error"
    }
}
