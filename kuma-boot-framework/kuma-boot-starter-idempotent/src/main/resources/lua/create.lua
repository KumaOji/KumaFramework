local uniqueKeyAndVersion = KEYS[1]
local uniqueKeyAndData = KEYS[2]
local expectVersion = tonumber(ARGV[1])
local expireTime = tonumber(ARGV[2])
local data = ARGV[3]

local existRecord = redis.call('EXISTS',uniqueKeyAndVersion)
-- 记录已经存在，create失败
if existRecord == 1 then
    return 0
else
    -- 不存在该记录
    if expireTime <= 0 then
        -- 过期时间小于0 则说明记录永不过期
      redis.call('SET', uniqueKeyAndVersion, expectVersion)
      redis.call('SET', uniqueKeyAndData, data)
    else
      redis.call('SETEX', uniqueKeyAndVersion, expireTime, expectVersion)
      redis.call('SETEX', uniqueKeyAndData, expireTime, data)
    end
    return 1
end
