public class Solution {
    public int[] twoSum(int[] nums, int target) {
        HashMap cache = new HashMap(4096, 1);
        int[] indices = new int[2];
        for (int i = 0; i < nums.length; i += 1) {
            if (cache.get(nums[i]) != null) {
                indices[0] = (int) cache.get(nums[i]);
                indices[1] = i;
                return indices;
            }
            cache.put(target - nums[i], i);
        }
        return indices;
    }
}
