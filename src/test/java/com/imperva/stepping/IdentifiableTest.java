package com.imperva.stepping;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;

/**
 * @author nir.usha
 * @since 05/05/2020
 */
class IdentifiableTest {

    @Test
    void get() {
        Identifiable identifiable = new Identifiable(2, "id");
        Assert.assertEquals(2, identifiable.get());
    }

    @Test
    void getId() {
        Identifiable identifiable = new Identifiable(2, "id");
        Assert.assertEquals("id", identifiable.getId());
    }

    //TODO junit: remove the ignore, hashCode and equals should sync
    //from hashCode documentation:
    //     *     If two objects are equal according to the {@code equals(Object)}
    //     *     method, then calling the {@code hashCode} method on each of
    //     *     the two objects must produce the same integer result.
    @Ignore
    @Test
    void hashCode_and_Equals() {
        Identifiable identifiable1 = new Identifiable(2, "my_id");
        Identifiable identifiable2 = new Identifiable(4, "My_ID");
        Assert.assertEquals(identifiable1, identifiable2);
        Assert.assertEquals(identifiable1.hashCode(), identifiable2.hashCode());
    }
}