import { getUserWallet, depositMoney } from '@/Redux/Wallet/Action'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { CheckCircle, Home, Wallet } from 'lucide-react'
import { useEffect, useState } from 'react'
import { useDispatch, useSelector } from 'react-redux'
import { useSearchParams, useNavigate } from 'react-router-dom'

const PaymentSuccess = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { wallet } = useSelector((store) => store);
  const [searchParams] = useSearchParams();
  const [paymentData, setPaymentData] = useState(null);
  
  const paymentId = searchParams.get("razorpay_payment_id");
  const referenceId = searchParams.get("razorpay_payment_link_reference_id");
  const paymentStatus = searchParams.get("razorpay_payment_link_status");

  useEffect(() => {
    const fetchDeposit = async () => {
      const jwt = localStorage.getItem("jwt");
      if (!jwt) {
         navigate("/signin");
      } else if (paymentId && referenceId && paymentStatus === "paid") {
        // Dispatch deposit money action to backend to confirm payment
        const data = await dispatch(depositMoney({
          jwt: jwt,
          orderId: referenceId,
          paymentId: paymentId
        }));
        setPaymentData(data);
      }
    };
    fetchDeposit();
  }, [paymentId, referenceId, dispatch, navigate]);

  return (
    <div className="min-h-screen bg-background flex flex-col justify-center items-center p-4">
      <Card className="w-full max-w-md border-none shadow-2xl bg-card">
        <CardHeader className="flex flex-col items-center pb-6">
          <div className="h-20 w-20 bg-green-500/20 rounded-full flex items-center justify-center mb-4">
            <CheckCircle className="h-10 w-10 text-green-500" />
          </div>
          <CardTitle className="text-2xl font-bold text-center">Payment Successful!</CardTitle>
          <p className="text-muted-foreground text-center">Your transaction has been processed successfully.</p>
        </CardHeader>
        
        <CardContent className="space-y-6">
          <div className="bg-muted/50 rounded-lg p-4 space-y-3">
            <div className="flex justify-between items-center">
              <span className="text-sm text-muted-foreground">Payment ID</span>
              <span className="font-medium text-sm break-all">{paymentId}</span>
            </div>
            <div className="flex justify-between items-center">
              <span className="text-sm text-muted-foreground">Reference ID</span>
              <span className="font-medium text-sm">{referenceId}</span>
            </div>
            <div className="flex justify-between items-center">
              <span className="text-sm text-muted-foreground">Status</span>
              <span className="text-green-500 font-medium capitalize">{paymentStatus}</span>
            </div>
            {paymentData && (
              <>
                <div className="flex justify-between items-center">
                  <span className="text-sm text-muted-foreground">Amount Paid</span>
                  <span className="font-medium text-sm">${paymentData.amount}</span>
                </div>
                <div className="flex justify-between items-center">
                  <span className="text-sm text-muted-foreground">Bank/Method</span>
                  <span className="font-medium text-sm capitalize">{paymentData.paymentMethod} {paymentData.paymentDetails ? `- ${paymentData.paymentDetails}` : ''}</span>
                </div>
              </>
            )}
          </div>

          <div className="bg-primary/10 rounded-lg p-4 flex justify-between items-center">
             <div className="flex items-center gap-3">
                <div className="bg-primary/20 p-2 rounded-full">
                    <Wallet className="h-5 w-5 text-primary" />
                </div>
                <div>
                    <p className="text-sm font-medium">New Wallet Balance</p>
                    <p className="text-xs text-muted-foreground">Updated instantly</p>
                </div>
             </div>
             <p className="text-xl font-bold">${wallet.userWallet?.balance || "0.00"}</p>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <Button variant="outline" className="w-full" onClick={() => navigate("/")}>
              <Home className="mr-2 h-4 w-4" /> Home
            </Button>
            <Button className="w-full" onClick={() => navigate("/wallet")}>
              <Wallet className="mr-2 h-4 w-4" /> Wallet
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}

export default PaymentSuccess