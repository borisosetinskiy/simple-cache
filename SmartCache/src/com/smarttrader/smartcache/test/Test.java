package com.smarttrader.smartcache.test;

import com.smarttrader.smartcache.*;

import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Test {

    static final String[] KEYS = new String[]{"a","b","c","d","e", "f"};

    public static void main(String [] args)throws Exception{
        TestResource testResource = new TestResource(KEYS);
        SmartCacheKeyCollection<String> smartCacheKeyCollection = new SmartCacheKeyCollectionImpl(KEYS);
        SmartCache smartCache = new SmartCacheImpl(smartCacheKeyCollection);
        ExecutorService executorService = Executors.newCachedThreadPool();
        int i =0;
        for(; i< KEYS.length+10; i++){
            if(i > KEYS.length+4){
                try{
                    Thread.sleep(100);
                }catch(Exception e){

                }
            }
            executorService.execute(new TestRunnable(""+i, smartCache, testResource));
        }
        try{
            Thread.sleep(20000);
        }catch(Exception e){

        }

        int j = i + 5;
        for( ; i< j; i++){

            executorService.execute(new TestRunnable(""+i, smartCache, testResource));
        }

    }

    static class TestRunnable implements Runnable, SmartCacheLoader<String , Map<String, TestValue>,  TestValue >, SmartCacheListener<String, TestValue> {

        final String name;
        public TestRunnable(String name, SmartCache smartCache, TestResource testResource) {
            this.name = name;
            this.smartCache = smartCache;
            this.testResource = testResource;
        }
        final SmartCache  smartCache;
        final TestResource testResource;
        @Override
        public void run() {
            Collection<TestValue> result =  smartCache.loadValue(this, this);
            if(result == null){
                System.out.println(String.format("End name %s not result", name));
            }else{
                System.out.println(String.format("End name %s Yes result", name));
            }
        }
        @Override
        public void load(SmartCacheKey<String> key, Map<String, TestValue> stringTestValueMap, SmartCacheListener<String, TestValue> listener) {
            String load = testResource.get(key.getKey());
            if(load!=null){
                int size = key.size();
                int totalSize = key.totalSize();
                TestValue testValue = new TestValue();
                testValue.setValue(load);
                stringTestValueMap.put(key.getKey(), testValue);
                key.setLoaded();
                listener.onSnapshot(new SmartCacheSnapshot<TestValue>() {
                    @Override
                    public Collection<TestValue> getValue() {
                        return stringTestValueMap.values();
                    }
                }, totalSize == ++size);
            }
        }

        @Override
        public void onSnapshot(SmartCacheSnapshot<TestValue> snapshot, boolean end) {
            for(TestValue testValue :snapshot.getValue()) {
                System.out.println(String.format("Name %s value %s end %s ", name, testValue.getValue(), end));
            }
        }

        @Override
        public void onUpdateSnapshot(SmartCacheSnapshot<TestValue> snapshot, String key) {

        }


    }


    static class TestResource{
        Map<String, String> resource = new ConcurrentHashMap<>();
        public TestResource(String[] keys){
           for(String key: keys){
               resource.put(key, generate(keys));
           }
        }
        String generate(String[] keys){
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(System.currentTimeMillis());
            int size = random.nextInt(50)+10;
            for(int i = 0; i < size; i++){
                stringBuilder.append(keys[next(keys)]);
            }
            return stringBuilder.toString();
        }
        Random random = new Random();
        public String get(String key){
            try{
                int delay = random.nextInt(1000*15)+(1000*5);
                System.out.println("Delay "+delay+ " for key "+key);
                Thread.sleep(delay);
            }catch (Exception e){}
            return resource.get(key);
        }

        private AtomicInteger index = new AtomicInteger();

        private int next(String [] keys){
            return index.updateAndGet(operand -> {
                if(operand >= keys.length-1)
                    return 0;
                return ++operand;
            });
        }
    }
    static class TestValue{
        String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
