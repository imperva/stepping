package com.imperva.stepping;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

class ContainerDefaultImplTest {

    ContainerDefaultImpl containerDefault;

    @BeforeEach
    void setUp() {
        containerDefault = new ContainerDefaultImpl();
    }

    @Test
    void add_duplicateStrId() {
        Assertions.assertThrows(IdentifiableSteppingException.class, () -> {
            containerDefault.add("test");
            containerDefault.add("test");
        });
    }

    @Test
    void add_duplicateIntegerId2() {
        Assertions.assertThrows(IdentifiableSteppingException.class, () -> {
            containerDefault.add(2);
            containerDefault.add(2);
        });
    }

    @Test
    void add_caseSensitiveId() {
        containerDefault.add("test");
        containerDefault.add("Test");
    }

    @Test
    void add2_duplicateStrId() {
        Assertions.assertThrows(IdentifiableSteppingException.class, () -> {
            containerDefault.add(new Object(), "test");
            containerDefault.add(new Object(), "test");
        });
    }

    @Test
    void add2_duplicateIntegerId2() {
        Assertions.assertThrows(IdentifiableSteppingException.class, () -> {
            containerDefault.add(2, "test");
            containerDefault.add(2, "test");
        });
    }

    @Test
    void add2_caseSensitiveId() {
        containerDefault.add(new Object(), "test");
        containerDefault.add(new Object(), "Test");
    }

    @Test
    void add3_duplicateStrId() {
        Assertions.assertThrows(IdentifiableSteppingException.class, () -> {
            containerDefault.add(new Identifiable(new Object(), "test"));
            containerDefault.add(new Identifiable(new Object(), "test"));
        });
    }

    @Test
    void add3_duplicateIds() {
        Assertions.assertThrows(IdentifiableSteppingException.class, () -> {
            containerDefault.add(new Identifiable(2, "test"));
            containerDefault.add(new Identifiable(2, "test"));
        });
    }

    @Test
    void add3_caseSensitiveId() {
        containerDefault.add(new Identifiable(new Object(), "test"));
        containerDefault.add(new Identifiable(new Object(), "Test"));
    }

    @Test
    void add4_caseSensitiveId() {
        containerDefault.add(new HashMap() {{
            put("val1", "id");
            put("val2", "ID");
        }});
    }

    @Test
    void add4_nullHashMap() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            containerDefault.add((HashMap) null);
        });
    }

    @Test
    void add5() {
        List<Identifiable> identifiables = Arrays.asList(
                new Identifiable(1, "id1"),
                new Identifiable(2, "id2"));

        containerDefault.add(identifiables);

        Assert.assertEquals(identifiables.size(), containerDefault.size());
    }

    @Test
    void add5_duplicateIds() {
        Assertions.assertThrows(IdentifiableSteppingException.class, () -> {
            List<Identifiable> identifiables = Arrays.asList(
                    new Identifiable(1, "id"),
                    new Identifiable(new Object(), "id"));

            containerDefault.add(identifiables);
        });
    }

    @Test
    void add5_nullList() {
        Assertions.assertThrows(SteppingException.class, () -> {
            containerDefault.add((List) null);
        });
    }

    @Test
    void remove() {
        containerDefault.add(1, "test1");
        containerDefault.add(2, "test2");
        containerDefault.add(3, "test3");
        containerDefault.remove("test1").remove("test2");

        Assert.assertEquals(1, containerDefault.size());
        Assert.assertEquals(3, (int) containerDefault.getById("test3"));
    }

    @Test
    void getById() {
        containerDefault.add(1, "test1");
        containerDefault.add(2, "test2");

        Assert.assertEquals(2, (int) containerDefault.getById("test2"));
    }

    @Test
    void getById_caseSensitive() {
        containerDefault.add(1, "test");
        containerDefault.add(2, "TeSt");

        Assert.assertEquals(2, (int) containerDefault.getById("TeSt"));
    }

    @Test
    void getSonOf_interface() {
        containerDefault.add("val");
        containerDefault.add(3);

        List<Object> sonOf = containerDefault.getSonOf(CharSequence.class);
        Assert.assertEquals(1, sonOf.size());
    }

    @Test
    void getSonOf_object() {
        containerDefault.add("val");
        containerDefault.add(3);

        List<Object> sonOf = containerDefault.getSonOf(Object.class);
        Assert.assertEquals(2, sonOf.size());
    }

    @Test
    void getSonOf_stringClass() {
        containerDefault.add("val");
        containerDefault.add(3);

        List<Object> sonOf = containerDefault.getSonOf(String.class);
        Assert.assertEquals(1, sonOf.size());
    }

    @Test
    void getSonOf_null() {
        containerDefault.add("val");
        containerDefault.add(3);

        List<Object> sonOf = containerDefault.getSonOf(null);
        Assert.assertEquals(0, sonOf.size());
    }

    @Test
    void getSonOf_notExist() {
        containerDefault.add("val");
        containerDefault.add(3);

        List<Object> sonOf = containerDefault.getSonOf(Stream.class);
        Assert.assertEquals(0, sonOf.size());
    }

    @Test
    void getTypeOf_sameClass() {
        containerDefault.add("val");
        containerDefault.add(3);

        List<Object> typeOf = containerDefault.getTypeOf(String.class);
        Assert.assertEquals(1, typeOf.size());
    }

    @Test
    void getTypeOf_interface() {
        containerDefault.add("val");
        containerDefault.add(3);

        List<Object> typeOf = containerDefault.getTypeOf(CharSequence.class);
        Assert.assertEquals(0, typeOf.size());
    }

    @Test
    void getTypeOf_null() {
        containerDefault.add("val");
        containerDefault.add(3);

        List<Object> typeOf = containerDefault.getTypeOf(null);
        Assert.assertEquals(0, typeOf.size());
    }

    @Test
    void getAll() {
        containerDefault.add("val");
        containerDefault.add(3);

        List<Identifiable> all = containerDefault.getAll();
        Assert.assertEquals(2, all.size());
    }

    @Test
    void getAll_ensureCloned() {
        containerDefault.add("val");
        containerDefault.add(3);

        List<Identifiable> all = containerDefault.getAll();
        all.remove(0);

        List<Identifiable> allCloned = containerDefault.getAll();
        Assert.assertEquals(2, allCloned.size());
    }

    @Test
    void clearAndSize() {
        containerDefault.add("val");
        containerDefault.add(3);

        containerDefault.clear();
        Assert.assertEquals(0, containerDefault.size());
    }

    @Test
    void exist() {
        containerDefault.add(1, "id1");
        containerDefault.add(2, "id2");
        containerDefault.add(3);

        Assert.assertTrue(containerDefault.exist("id2"));
    }

    @Test
    void exist_caseSensitive() {
        containerDefault.add(1, "id1");
        containerDefault.add(2, "id2");
        containerDefault.add(3);

        Assert.assertFalse(containerDefault.exist("ID2"));
    }

    @Test
    void exist_notExist() {
        containerDefault.add(1, "id1");
        containerDefault.add(2, "id2");
        containerDefault.add(3);

        Assert.assertFalse(containerDefault.exist("id3"));
    }
}