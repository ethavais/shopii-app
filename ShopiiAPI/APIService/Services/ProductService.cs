using APIService.Models;
using APIService.Repositories;

namespace APIService.Services
{
    public class ProductService : IProductService
    {
        private readonly IProductRepository _productRepository;

        public ProductService(IProductRepository productRepository)
        {
            _productRepository = productRepository;
        }

        public async Task<IEnumerable<Product>> GetProductsAsync()
        {
            return await _productRepository.GetAllAsync();
        }

        public async Task<IEnumerable<Product>> GetProductsByCategoryAsync(ProductCategory category)
        {
            return await _productRepository.GetProductsByCategoryAsync(category);
        }

        public async Task<IEnumerable<Product>> GetProductsByPriceRangeAsync(double minPrice, double maxPrice)
        {
            return await _productRepository.GetProductsByPriceRangeAsync(minPrice, maxPrice);
        }

        public async Task<IEnumerable<Product>> GetProductsByRatingAsync(double minRating)
        {
            return await _productRepository.GetProductsByRatingAsync(minRating);
        }
    }

    public interface IProductService
    {
        Task<IEnumerable<Product>> GetProductsAsync();
        Task<IEnumerable<Product>> GetProductsByCategoryAsync(ProductCategory category);
        Task<IEnumerable<Product>> GetProductsByPriceRangeAsync(double minPrice, double maxPrice);
        Task<IEnumerable<Product>> GetProductsByRatingAsync(double minRating);
    }
} 