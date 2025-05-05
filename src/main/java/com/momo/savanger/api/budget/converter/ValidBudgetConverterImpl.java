package com.momo.savanger.api.budget.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.momo.savanger.api.budget.BudgetService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidBudgetConverterImpl extends JsonDeserializer<Object> {

    private final BudgetService budgetService;

    @Override
    public Object deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {
        final String val = parser.getValueAsString();
        if (val == null || val.trim().isEmpty()) {
            return null;
        }

        try {
            return this.budgetService.findIfValid(Long.parseLong(val.trim())).orElse(null);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

}
