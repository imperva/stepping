package com.imperva.stepping;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author nir.usha
 * @since 24/05/2020
 */
class ShouterTest {

    private final static String SUBJECT_TYPE = "SUBJECT";
    private final static String SUBJECT_TYPE_NOT_EXIST = "SUBJECT_NOT_EXIST";

    private int publishCounter = 0;
    private int exceptionHandlerCounter = 0;
    private Object objValue;
    private Data dataValue;

    private Subject subject;
    private Container container;
    private IExceptionHandler rootExceptionHandler;
    private Shouter shouter;


    @BeforeEach
    void setUp() {

        objValue = new Object();
        dataValue = new Data(null);
        //create subject
        subject = mock(Subject.class);
        doAnswer(a -> publishCounter++).when(subject).publish(any(Data.class));
        doAnswer(a -> publishCounter++).when(subject).publish(eq(objValue));

        //create container
        container = mock(Container.class);
        when(container.getById(SUBJECT_TYPE)).then(a -> subject);

        //create exception handler
        rootExceptionHandler = mock(IExceptionHandler.class);
        doAnswer(a -> {exceptionHandlerCounter++; return true;}).when(rootExceptionHandler).handle(any(SteppingDistributionException.class));

        //create shouter
        shouter = new Shouter("Test",container, rootExceptionHandler);
    }

    @Test
    void shout1() {
        shouter.shout(SUBJECT_TYPE, objValue);
        Assert.assertEquals(1, publishCounter);
        Assert.assertEquals(0, exceptionHandlerCounter);
    }

    @Test
    void shout1_subjectNotExist() {
        shouter.shout(SUBJECT_TYPE_NOT_EXIST, objValue);
        Assert.assertEquals(0, publishCounter);
        Assert.assertEquals(0, exceptionHandlerCounter);
    }

    @Test
    void shout2() {
        shouter.shout(SUBJECT_TYPE, dataValue);
        Assert.assertEquals(1, publishCounter);
        Assert.assertEquals(0, exceptionHandlerCounter);
    }

    @Test
    void shout2_subjectNotExist() {
        shouter.shout(SUBJECT_TYPE_NOT_EXIST, dataValue);
        Assert.assertEquals(0, publishCounter);
        Assert.assertEquals(0, exceptionHandlerCounter);
    }
}