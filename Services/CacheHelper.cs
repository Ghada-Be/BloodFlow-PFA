using Microsoft.Extensions.Caching.Memory;

namespace BloodFlow.MS3.Services;

public class CacheHelper
{
    private readonly IMemoryCache _cache;
    private readonly IConfiguration _configuration;

    public CacheHelper(IMemoryCache cache, IConfiguration configuration)
    {
        _cache = cache;
        _configuration = configuration;
    }

    public MemoryCacheEntryOptions Options(int? minutes = null, int? seconds = null)
    {
        var defaultMinutes = _configuration.GetValue<int>("CacheSettings:DefaultExpirationMinutes", 5);
        var duration = seconds.HasValue
            ? TimeSpan.FromSeconds(seconds.Value)
            : TimeSpan.FromMinutes(minutes ?? defaultMinutes);

        return new MemoryCacheEntryOptions
        {
            AbsoluteExpirationRelativeToNow = duration,
            SlidingExpiration = TimeSpan.FromMinutes(1)
        };
    }

    public void Remove(params string[] keys)
    {
        foreach (var key in keys)
            _cache.Remove(key);
    }
}
