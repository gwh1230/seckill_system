-- redis lua测试脚本，可以将多个redis的操作，转换成原子操作
if redis.call("get",KEYS[1]==ARGV[1]) then
    return redis.call("del",KEYS[1])
else
    return 0
end