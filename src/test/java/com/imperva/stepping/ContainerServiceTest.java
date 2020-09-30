package com.imperva.stepping;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Author: Linda Nasredin
 * Date: 06 May 2020
 */
class ContainerServiceTest {

    @Test
    void changeDelay_delay_changed() {
        RunningScheduled runningScheduled = new RunningScheduled("test", 2, 2, TimeUnit.SECONDS, () -> {
        });

        Container innerContainer = new ContainerService();
        innerContainer.add(runningScheduled, "stepId3" + ContainerService.RUNNING_SCHEDULED);

        ContainerService containerService = new ContainerService();
        containerService.add(innerContainer, ContainerService.STEPPING_PRIVATE_CONTAINER);
        runningScheduled.awake();
        containerService.changeDelay("stepId3", 20, 20, TimeUnit.SECONDS);
        RunningScheduled e = ((RunningScheduled) ((Container) containerService.getById(ContainerService.STEPPING_PRIVATE_CONTAINER)).getById("stepId3" + ContainerService.RUNNING_SCHEDULED));
        runningScheduled.close();

        Assertions.assertNotEquals(2, e.getDelay(TimeUnit.SECONDS));
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