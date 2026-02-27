local uniqueKeyAndVersion = KEYS[1]
local uniqueKeyAndData = KEYS[2]

local result = redis.call('del',uniqueKeyAndVersion, uniqueKeyAndData)
return 1
