package com.kuma.cloud.leetcode.doublePoint;

import java.util.*;

/**
 *
 */
public class maxOperations {
    public int maxOperations(int[] nums, int k) {
        Arrays.sort(nums);
        int left = 0, right = nums.length - 1;
        int count = 0;

        while (left < right) {
            int sum = nums[left] + nums[right];
            if (sum == k) {
                count++;
                left++;
                right--;
            } else if (sum < k) {
                left++;
            } else {
                right--;
            }
        }
        return count;
    }
    public int maxOperations2(int[] nums, int k) {
        if (nums == null || nums.length < 2) {
            return 0;
        }
        boolean[] removed = new boolean[nums.length];
        int count = 0;
        for (int i = 0; i < nums.length; i++) {
            if (removed[i]) continue;
            for (int j = i + 1; j < nums.length; j++) {
                if (removed[j]) continue;
                if (nums[i] + nums[j] == k) {
                    count++;
                    removed[i] = true;
                    removed[j] = true;
                    break;
                }
            }
        }
        List<Integer> remaining = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            if (!removed[i]) {
                remaining.add(nums[i]);
            }
        }
        Iterator<Object> iterator = Arrays.stream(remaining.toArray()).iterator();
        while(iterator.hasNext()){
            System.out.println(iterator.next());
        }
        return count;
    }
}
