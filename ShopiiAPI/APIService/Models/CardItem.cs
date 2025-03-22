using System.ComponentModel.DataAnnotations;

namespace APIService.Models
{
    public class CardItem
    {
        [Key]
        public Guid Id { get; set; }
        public int Quantity { get; set; }
        public int TotalPrice { get; set; }
        public Guid ProductId { get; set; }
        public Guid PaymentId { get; set; }
        public virtual Payment Payment { get; set; }
        public virtual Product Product { get; set; }


    }
}
