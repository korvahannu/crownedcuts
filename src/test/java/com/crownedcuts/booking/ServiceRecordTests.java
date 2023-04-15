package com.crownedcuts.booking;

import com.crownedcuts.booking.records.Service;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ServiceRecordTests {
    @Test
    void canCreateServiceRecord()
    {
        var service = new Service("testService", "testi", "test", 50.5, true);
        Assertions.assertEquals("testService", service.id());
        Assertions.assertEquals("testi", service.name());
        Assertions.assertEquals("test", service.name_en());
        Assertions.assertEquals(50.5, service.price());
        Assertions.assertEquals(true, service.isBarberService());
    }
}
