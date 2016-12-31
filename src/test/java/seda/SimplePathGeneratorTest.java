package seda;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SimplePathGeneratorTest {

    @org.junit.Test
    public void fixMe0() {
        List<String> alist = Arrays.asList("a", "b", "c", "d", "e");
        List<String> blist = Arrays.asList("f", "g", "h");
        List<String> glist = Arrays.asList("j", "k", "o");
        Multimap<String, String> graph = ArrayListMultimap.create();
        graph.putAll("a", alist);
        graph.putAll("b", blist);
        graph.putAll("h", glist);
        Set<String> conditions = Sets.newHashSet("b", "d");
        Set<String> parents = Sets.newHashSet("b", "h");
        SimplePathGenerator gen = new SimplePathGenerator("", "", graph, conditions, parents);
        List<String> parsedList = gen.generatePaths("a");
        assertEquals(4, parsedList.size());
        assertTrue(parsedList.contains("ace"));
        assertTrue(parsedList.contains("acde"));
        assertTrue(parsedList.contains("abfghjkoce"));
        assertTrue(parsedList.contains("abfghjkocde"));
    }

    @Test
    public void testLastIsConditional3level() {
        Set<String> conditions = Sets.newHashSet("d", "f");
        Set<String> parents = Sets.newHashSet("d", "f");
        Multimap<String, String> graph = ArrayListMultimap.create();
        List<String> alist = Arrays.asList("a", "d");
        List<String> dlist = Arrays.asList("e", "f");
        List<String> glist = Collections.singletonList("g");
        graph.putAll("a", alist);
        graph.putAll("d", dlist);
        graph.putAll("f", glist);
        SimplePathGenerator gen = new SimplePathGenerator("", "", graph, conditions, parents);
        List<String> parsedList = gen.generatePaths("a");
        parsedList.forEach(System.out::println);
        assertTrue(parsedList.contains("a"));
        assertTrue(parsedList.contains("ade"));
        assertTrue(parsedList.contains("adefg"));
    }

    @Test
    public void testLastIsConditional2level() {
        Set<String> conditions = Sets.newHashSet("f");
        Set<String> parents = Sets.newHashSet("f");
        Multimap<String, String> graph = ArrayListMultimap.create();
        List<String> dlist = Arrays.asList("e", "f");
        List<String> glist = Collections.singletonList("g");
        graph.putAll("d", dlist);
        graph.putAll("f", glist);
        SimplePathGenerator gen = new SimplePathGenerator("", "", graph, conditions, parents);
        List<String> parsedList = gen.generatePaths("d");
        assertTrue(parsedList.contains("e"));
        assertTrue(parsedList.contains("efg"));
    }

    @org.junit.Test
    public void fixMe1() {
        List<String> alist = Arrays.asList("a", "b", "c", "d", "e");
        List<String> blist = Arrays.asList("f", "g", "h");
        Multimap<String, String> graph = ArrayListMultimap.create();
        graph.putAll("a", alist);
        graph.putAll("b", blist);
        Set<String> conditions = Sets.newHashSet("b", "d");
        Set<String> parents = Sets.newHashSet("b");
        SimplePathGenerator gen = new SimplePathGenerator("", "", graph, conditions, parents);
        List<String> parsedList = gen.generatePaths("a");
        assertEquals(4, parsedList.size());
        assertTrue(parsedList.contains("ace"));
        assertTrue(parsedList.contains("acde"));
        assertTrue(parsedList.contains("abfghce"));
        assertTrue(parsedList.contains("abfghcde"));
    }

    @org.junit.Test
    public void fixMe2() {
        List<String> alist = Arrays.asList("a", "b", "c");
        Multimap<String, String> graph = ArrayListMultimap.create();
        graph.putAll("a", alist);
        Set<String> conditions = Sets.newHashSet("b");
        Set<String> parents = Sets.newHashSet();
        SimplePathGenerator gen = new SimplePathGenerator("->", "start", graph, conditions, parents);
        List<String> parsedList = gen.generatePaths("a");
        assertEquals(2, parsedList.size());
        assertTrue(parsedList.contains("start->a->b->c"));
        assertTrue(parsedList.contains("start->a->c"));
    }

    @org.junit.Test
    public void fixMe3() {
        List<String> list = Arrays.asList("a", "b", "c", "d", "e");
        Multimap<String, String> graph = ArrayListMultimap.create();
        graph.putAll("a", list);
        Set<String> conditions = Sets.newHashSet("b", "d", "e");
        Set<String> parents = Sets.newHashSet();
        SimplePathGenerator gen = new SimplePathGenerator("", "", graph, conditions, parents);
        List<String> parsedList = gen.generatePaths("a");
        parsedList.forEach(System.out::println);
        assertEquals(8, parsedList.size());
        assertTrue(parsedList.contains("abcde"));
        assertTrue(parsedList.contains("abcd"));
        assertTrue(parsedList.contains("abce"));
        assertTrue(parsedList.contains("abc"));
        assertTrue(parsedList.contains("acde"));
        assertTrue(parsedList.contains("acd"));
        assertTrue(parsedList.contains("ace"));
        assertTrue(parsedList.contains("ac"));
    }
}