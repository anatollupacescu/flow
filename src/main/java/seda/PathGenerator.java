package seda;

import java.util.List;

interface PathGenerator<A, B> {

    List<A> generatePaths(B object);
}
