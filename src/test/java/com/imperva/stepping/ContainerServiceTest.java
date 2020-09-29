package com.imperva.stepping;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Author: Linda Nasredin
 * Date: 06 May 2020
 */
class ContainerServiceTest {

    @Test
    void getTickCallbackRunning() {
        RunningScheduled value = mock(RunningScheduled.class);

        Container innerContainer = new ContainerService();
        innerContainer.add(value, "stepId3" + ContainerService.RUNNING_SCHEDULED);

        ContainerService containerService = new ContainerService();
        containerService.add(innerContainer, ContainerService.STEPPING_PRIVATE_CONTAINER);

        RunningScheduled actual = containerService.getTickCallbackRunning("stepId3");

        Assertions.assertEquals(value, actual);
    }

    @Test
    void getQSize() {
        IStepDecorator decoratorMock = mock(IStepDecorator.class);
        when(decoratorMock.getQSize()).thenReturn(34);

        Container innerContainer = new ContainerService();
        innerContainer.add(decoratorMock, "stepId1" + ContainerService.DECORATOR);

        ContainerService containerService = new ContainerService();
        containerService.add(innerContainer, ContainerService.STEPPING_PRIVATE_CONTAINER);

        int qSize = containerService.getQSize("stepId1");

        Assertions.assertEquals(34, qSize);
    }
}