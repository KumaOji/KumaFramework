-- 1. 获取参数
local key = KEYS[1]
local uniqueCode = KEYS[2]
local currentTime = KEYS[3]
-- 2. 以数组最大值为 ttl 最大值
local expireTime = -1;
-- 3. 遍历数组查看是否超过限流规则
for i = 1, #ARGV, 2 do
    local rateRuleCount = tonumber(ARGV[i])
    local rateRuleTime = tonumber(ARGV[i + 1])
    -- 3.1 判断在单位时间内访问次数
    local count = redis.call('ZCOUNT', key, currentTime - rateRuleTime, currentTime)
    -- 3.2 判断是否超过规定次数
    if tonumber(count) >= rateRuleCount then
        return true
    end
    -- 3.3 判断元素最大值，设置为最终过期时间
    if rateRuleTime > expireTime then
        expireTime = rateRuleTime
    end
end
-- 4. redis 中添加当前时间
redis.call('ZADD', key, currentTime, uniqueCode)
-- 5. 更新缓存过期时间
redis.call('PEXPIRE', key, expireTime)
-- 6. 删除最大时间限度之前的数据，防止数据过多
redis.call('ZREMRANGEBYSCORE', key, 0, currentTime - expireTime)
return false
