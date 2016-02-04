package com.intel.icecp.module.sample;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TimeBasedSamplerTest {

    public int count;
    String objectReceived;

    @Test
    public void testConstructorWithNegativeNumber() {
        TimeBasedSampler sampler = new TimeBasedSampler(-1);
        assertEquals(sampler.getSamplingDuration(), TimeBasedSampler.DEFAULT_DURATION_SIZE);
    }

    @Test
    public void testFirstInObjectAlwaysReported() {
        count = 0;
        long sampleDuration = 1000000;

        TimeBasedSampler sampler = new TimeBasedSampler(sampleDuration);
        sampler.out(o -> count++);
        sampler.in(new Object());

        assertEquals(1, count);
    }

    @Test
    public void testMultipleInObjectsIn1Period() {
        count = 0;

        long sampleDuration = 1000000;

        TimeBasedSampler sampler = new TimeBasedSampler(sampleDuration);
        sampler.out(o -> objectReceived = o.toString());

        // Send a bunch of in objects in the first period
        for (int i = 0; i < 10; i++) {
            sampler.in(i);
        }

        // ensure the first object received is the one that was sent
        assertEquals("0", objectReceived);
    }

    @Test
    public void testDataAcrossMultiplePeriods() throws InterruptedException {
        count = 0;
        long sampleDuration = 100;
        int numberOfPeriods = 5;

        TimeBasedSampler sampler = new TimeBasedSampler(sampleDuration);
        sampler.out(Consumer -> count++);

        // don't run for the full number of periods...stop the last period early
        // so we can ensure
        // the in calls don't bleed into another period
        long runUntil = sampler.getFrom() + (sampleDuration * numberOfPeriods) - 25;
        while (System.currentTimeMillis() < runUntil) {
            sampler.in(new Object());
        }

        // check that we sampled 1 item for each time period
        assertEquals(numberOfPeriods, count);
    }
}
