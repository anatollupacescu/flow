package seda;

import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class FlowPathGeneratorTest {

    enum Test implements SedaType {
        UNU
    }

    private final FlowFormatter formatter = new FlowFormatter("->");
    private final FlowPathGenerator generator = new FlowPathGenerator(formatter);

    @org.junit.Test
    public void main() {
        Flow c1 = Flow.newWithName("c1").inFields(Test.UNU).build();
        Flow c2 = Flow.newWithName("c2").inFields(Test.UNU).build();
        Flow c3 = Flow.newWithName("c3").inFields(Test.UNU).build();
        Flow main = Flow.newWithName("test")
                .inFields(Test.UNU)
                .consumer(c1)
                .ifTrue("key", c2)
                .consumer(c3)
                .build();
        List<String> list = generator.generatePaths(main);
        assertTrue(list.contains("start->c1->c3"));
        assertTrue(list.contains("start->c1->(key)c2->c3"));
    }

    @org.junit.Test
    public void main2() {
        Flow c0 = Flow.newWithName("c0").inFields(Test.UNU).build();
        Flow c01 = Flow.newWithName("c01").inFields(Test.UNU).build();
        Flow c1 = Flow.newWithName("c1").inFields(Test.UNU).build();
        Flow c3 = Flow.newWithName("c3").inFields(Test.UNU).build();

        Flow c2 = Flow.newWithName("c2")
                .inFields(Test.UNU)
                .consumer(c0)
                .build();
        Flow c21 = Flow.newWithName("c21")
                .inFields(Test.UNU)
                .consumer(c01)
                .build();

        Flow main = Flow.newWithName("test")
                .inFields(Test.UNU)
                .consumer(c1)
                .ifTrue("key1", c2)
                .consumer(c3)
                .ifTrue("key2", c21)
                .build();
        List<String> list = generator.generatePaths(main);
        assertTrue(list.contains("start->c1->c3"));
        assertTrue(list.contains("start->c1->(key1)c2->c0->c3"));
        assertTrue(list.contains("start->c1->c3->(key2)c21->c01"));
        assertTrue(list.contains("start->c1->(key1)c2->c0->c3->(key2)c21->c01"));
    }

    @org.junit.Test
    public void test0() {
        Flow c1 = Flow.newWithName("c1").inFields(Test.UNU).build();
        Flow c2 = Flow.newWithName("c2").inFields(Test.UNU).build();
        Flow flow = Flow.newWithName("test")
                .inFields(Test.UNU)
                .consumer(c1)
                .consumer(c2).build();

        List<String> paths = generator.generatePaths(flow);
        assertNotNull(paths);
        Iterator<String> iterator = paths.iterator();
        assertTrue(iterator.hasNext());
        assertEquals("start->c1->c2", iterator.next());
        assertFalse(iterator.hasNext());
    }

    @org.junit.Test
    public void test1() {
        Flow c1 = Flow.newWithName("c1").inFields(Test.UNU).build();
        Flow c2 = Flow.newWithName("c2").inFields(Test.UNU).build();
        Flow flow = Flow.newWithName("test")
                .inFields(Test.UNU)
                .consumer(c1)
                .ifTrue("key", c2).build();

        List<String> paths = generator.generatePaths(flow);
        assertNotNull(paths);
        assertEquals(2, paths.size());
        assertTrue(paths.contains("start->c1"));
        assertTrue(paths.contains("start->c1->(key)c2"));
    }

    @org.junit.Test
    public void test2() {
        Flow c1 = Flow.newWithName("c1").inFields(Test.UNU).build();
        Flow c2 = Flow.newWithName("c2").inFields(Test.UNU).build();

        Flow c3 = Flow.newWithName("c3")
                .inFields(Test.UNU)
                .ifTrue("sub", c2)
                .build();

        Flow flow = Flow.newWithName("test")
                .inFields(Test.UNU)
                .consumer(c1)
                .ifTrue("key", c3).build();

        List<String> paths = generator.generatePaths(flow);
        assertNotNull(paths);
        Iterator<String> iterator = paths.iterator();
        assertTrue(iterator.hasNext());
        assertTrue(paths.contains("start->c1"));
        assertTrue(paths.contains("start->c1->(key)c3->(sub)c2"));
        assertTrue(paths.contains("start->c1->(key)c3"));
    }
}