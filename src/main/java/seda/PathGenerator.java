package seda;

import java.util.List;

public interface PathGenerator<A, B> {

    List<A> generatePaths(B object);
}
