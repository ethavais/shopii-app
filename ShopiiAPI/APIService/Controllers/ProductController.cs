using APIService.Models;
using APIService.Services;
using Microsoft.AspNetCore.Mvc;

namespace APIService.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class ProductController : ControllerBase
    {
        private readonly IProductService _productService;

        public ProductController(IProductService productService)
        {
            _productService = productService;
        }

        [HttpGet("products")]
        public async Task<ActionResult<IEnumerable<Product>>> GetProducts()
        {
            var products = await _productService.GetProductsAsync();
            return Ok(products);
        }

        [HttpGet("category/{category}")]
        public async Task<ActionResult<IEnumerable<Product>>> GetProductsByCategory(ProductCategory category)
        {
            var products = await _productService.GetProductsByCategoryAsync(category);
            return Ok(products);
        }

        [HttpGet("price-range")]
        public async Task<ActionResult<IEnumerable<Product>>> GetProductsByPriceRange([FromQuery] double minPrice, [FromQuery] double maxPrice)
        {
            var products = await _productService.GetProductsByPriceRangeAsync(minPrice, maxPrice);
            return Ok(products);
        }

        [HttpGet("rating")]
        public async Task<ActionResult<IEnumerable<Product>>> GetProductsByRating([FromQuery] double minRating)
        {
            var products = await _productService.GetProductsByRatingAsync(minRating);
            return Ok(products);
        }
    }
} 