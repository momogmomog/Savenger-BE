package com.momo.savanger.api.revision;

import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;

public interface RevisionService {

    Revision findById(Long id);

    Revision create(CreateRevisionDto dto) throws InvalidRecurrenceRuleException;

}
