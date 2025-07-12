import com.tohid.urlShortener.domain.Url
import com.tohid.urlShortener.utils.toBase62
import java.time.Instant

fun makeUrl(
    originalUrl: String = "https://example.com",
    shortUrl: String = 23L.toBase62(),
    expiryDate: Instant? = null
) = Url(
    originalUrl = originalUrl,
    shortUrl = shortUrl,
    expiryDate = expiryDate
)
