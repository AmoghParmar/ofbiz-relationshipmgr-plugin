package org.apache.ofbiz.relationshipmgr

import org.apache.ofbiz.entity.GenericValue
import org.apache.ofbiz.service.ServiceUtil

def createRmPerson() {
    Map result = ServiceUtil.returnSuccess()
    String partyId = parameters.partyId
    if (!partyId) {
        partyId = delegator.getNextSeqId("RmParty")
    }
    GenericValue rmParty = delegator.findOne("RmParty", [partyId: partyId], false)
    if (!rmParty) {
        rmParty = delegator.makeValue("RmParty", [partyId: partyId, partyTypeId: "PERSON"])
        rmParty.create()
    }
    GenericValue rmPerson = delegator.makeValue("RmPerson", [partyId: partyId])
    rmPerson.setNonPKFields(parameters)
    rmPerson.create()
    result.partyId = partyId
    return result
}
