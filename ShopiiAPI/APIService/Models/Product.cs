namespace APIService.Models
{
    public class Product
    {
        public Guid Id { get; set; }
        public string Name { get; set; }
        public string Description { get; set; }
        public double Price { get; set; }
        public List<string> ImageUrls { get; set; }
        public string Brand { get; set; }
        public ProductCategory Category { get; set; }
        public double Rating { get; set; }
        public int StockQuantity { get; set; }

        public Product()
        {
            Id = Guid.NewGuid();
            ImageUrls = new List<string>();
        }

        public Product(string name, string description, double price, List<string> imageUrls, string brand, ProductCategory category, double rating, int stockQuantity)
        {
            Id = Guid.NewGuid();
            Name = name;
            Description = description;
            Price = price;
            ImageUrls = imageUrls;
            Brand = brand;
            Category = category;
            Rating = rating;
            StockQuantity = stockQuantity;
        }
    }

    public enum ProductCategory
    {
        ELECTRONICS,
        CLOTHING,
        BOOKS,
        HOME,
        OTHER
    }
} 