local uniqueKeyAndVersion = KEYS[1]
local uniqueKeyAndData = KEYS[2]
local expectVersion = tonumber(ARGV[1])
local expireTime = tonumber(ARGV[2])
local data = ARGV[3]

local currentVersion = tonumber(redis.call('GET',uniqueKeyAndVersion))
-- 版本号相同说明未被更改
if currentVersion == expectVersion then
    -- 过期时间小于0 ，则任务记录永不过期
    if expireTime <= 0 then
        redis.call('SET',uniqueKeyAndVersion, expectVersion + 1)
        redis.call('SET',uniqueKeyAndData, data)
    else
        redis.call('SETEX',uniqueKeyAndVersion, expireTime, expectVersion + 1)
        redis.call('SETEX',uniqueKeyAndData, expireTime, data)
    end
    return 1;
else
    -- 版本号不同，则说明数据已被更改过了
    return 0;
end
