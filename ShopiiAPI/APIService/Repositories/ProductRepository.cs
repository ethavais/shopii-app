using APIService.Data;
using APIService.Models;
using Microsoft.EntityFrameworkCore;

namespace APIService.Repositories
{
    public class ProductRepository : GenericRepository<Product>, IProductRepository
    {
        public ProductRepository(ShopiiDbContext context) : base(context)
        {
        }

        public async Task<IEnumerable<Product>> GetProductsByCategoryAsync(ProductCategory category)
        {
            return await _context.Products
                .Where(p => p.Category == category)
                .ToListAsync();
        }

        public async Task<IEnumerable<Product>> GetProductsByPriceRangeAsync(double minPrice, double maxPrice)
        {
            return await _context.Products
                .Where(p => p.Price >= minPrice && p.Price <= maxPrice)
                .ToListAsync();
        }

        public async Task<IEnumerable<Product>> GetProductsByRatingAsync(double minRating)
        {
            return await _context.Products
                .Where(p => p.Rating >= minRating)
                .ToListAsync();
        }
    }

    public interface IProductRepository : IGenericRepository<Product>
    {
        Task<IEnumerable<Product>> GetProductsByCategoryAsync(ProductCategory category);
        Task<IEnumerable<Product>> GetProductsByPriceRangeAsync(double minPrice, double maxPrice);
        Task<IEnumerable<Product>> GetProductsByRatingAsync(double minRating);
    }
} 