/*
 * Copyright 2020  nils.hoffmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.isas.lipidomics.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author nils.hoffmann
 */
@EqualsAndHashCode
public class ModificationsList implements List<Pair<Integer, String>> {

    protected static final ModificationsList NONE = new ModificationsList(Collections.emptyList());

    public ModificationsList() {
        this.al = new ArrayList<>();
    }

    public ModificationsList(List<Pair<Integer, String>> list) {
        this.al = list;
    }

    private final List<Pair<Integer, String>> al;

    @Override
    public int size() {
        return al.size();
    }

    @Override
    public boolean isEmpty() {
        return al.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return al.contains(o);
    }

    @Override
    public int indexOf(Object o) {
        return al.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return al.lastIndexOf(o);
    }

    @Override
    public Object[] toArray() {
        return al.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return al.toArray(a);
    }

    @Override
    public Pair<Integer, String> get(int index) {
        return al.get(index);
    }

    @Override
    public Pair<Integer, String> set(int index, Pair<Integer, String> element) {
        return al.set(index, element);
    }

    @Override
    public boolean add(Pair<Integer, String> e) {
        return al.add(e);
    }

    @Override
    public void add(int index, Pair<Integer, String> element) {
        al.add(index, element);
    }

    @Override
    public Pair<Integer, String> remove(int index) {
        return al.remove(index);
    }

    @Override
    public boolean remove(Object o) {
        return al.remove(o);
    }

    @Override
    public void clear() {
        al.clear();
    }

    @Override
    public boolean addAll(Collection<? extends Pair<Integer, String>> c) {
        return al.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends Pair<Integer, String>> c) {
        return al.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return al.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return al.retainAll(c);
    }

    @Override
    public ListIterator<Pair<Integer, String>> listIterator(int index) {
        return al.listIterator(index);
    }

    @Override
    public ListIterator<Pair<Integer, String>> listIterator() {
        return al.listIterator();
    }

    @Override
    public Iterator<Pair<Integer, String>> iterator() {
        return al.iterator();
    }

    @Override
    public List<Pair<Integer, String>> subList(int fromIndex, int toIndex) {
        return al.subList(fromIndex, toIndex);
    }

    @Override
    public void forEach(Consumer<? super Pair<Integer, String>> action) {
        al.forEach(action);
    }

    @Override
    public Spliterator<Pair<Integer, String>> spliterator() {
        return al.spliterator();
    }

    @Override
    public boolean removeIf(Predicate<? super Pair<Integer, String>> filter) {
        return al.removeIf(filter);
    }

    @Override
    public void replaceAll(UnaryOperator<Pair<Integer, String>> operator) {
        al.replaceAll(operator);
    }

    @Override
    public void sort(Comparator<? super Pair<Integer, String>> c) {
        al.sort(c);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return al.containsAll(c);
    }

    @Override
    public String toString() {
        return al.toString();
    }

    @Override
    public <T> T[] toArray(IntFunction<T[]> generator) {
        return al.toArray(generator);
    }

    @Override
    public Stream<Pair<Integer, String>> stream() {
        return al.stream();
    }

    @Override
    public Stream<Pair<Integer, String>> parallelStream() {
        return al.parallelStream();
    }

    /**
     * Returns the total count (number of elements in this list) with that
     * particular key.
     *
     * @param modification the modification to count, e.g. "OH".
     * @return the number of times this modification occurs, or zero.
     */
    public Integer countFor(String modification) {
        return this.stream().filter((pair) -> {
            return pair.getValue().startsWith(modification);
        }).collect(Collectors.counting()).intValue();
    }

    /**
     * Returns the sum of the occurrence count for modifications with name "OH"
     * in the provided modifications list.
     *
     * @return the sum of the occurrences of OH.
     */
    public Integer countForHydroxy() {
        return countFor("OH");
    }

    /**
     * Returns the modification names stored in this modifications list.
     *
     * @return the modification names.
     */
    public List<String> keys() {
        return this.stream().map((t) -> {
            return t.getValue();
        }).collect(Collectors.toList());
    }

}
