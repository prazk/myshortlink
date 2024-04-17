local user_key = KEYS[1]
local max_requests = tonumber(ARGV[1])
local expire_seconds = tonumber(ARGV[2])

-- 检查计数器是否存在
local exists = redis.call("EXISTS", user_key)

if exists == 1 then
    -- 计数器存在，增加计数
    local current_requests = tonumber(redis.call("GET", user_key))
    if current_requests >= max_requests then
        -- 如果请求超过阈值，拒绝请求
        return 0
    else
        -- 请求次数未超过阈值，增加计数
        redis.call("INCR", user_key)
        return 1
    end
else
    -- 计数器不存在，创建并设置过期时间
    redis.call("SET", user_key, 1)
    redis.call("EXPIRE", user_key, expire_seconds)
    return 1
end
