import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import React, { useEffect, useState, useRef } from "react";
import { useDispatch, useSelector } from "react-redux";
import AccountVarificationForm from "./AccountVarificationForm";
import { VerifiedIcon, Upload } from "lucide-react";
import {
  enableTwoStepAuthentication,
  updateUserProfile,
  verifyOtp,
  fetchUserProfile,
  uploadProfileImage,
} from "@/Redux/Auth/Action";
import SpinnerBackdrop from "@/components/custome/SpinnerBackdrop";

const Profile = () => {
  const [isEditing, setIsEditing] = useState(false);
  const [previewImage, setPreviewImage] = useState(null);
  const [imageLoading, setImageLoading] = useState(false);
  const fileInputRef = useRef(null); // ✅ Create ref for file input
  const [formData, setFormData] = useState({
    fullName: "",
    dateOfBirth: "",
    nationality: "",
    address: "",
    city: "",
    postcode: "",
    country: "",
  });
  const { auth } = useSelector((store) => store);
  const dispatch = useDispatch();

  const handleEnableTwoStepVerification = (otp) => {
    console.log("EnableTwoStepVerification", otp);
    dispatch(
      enableTwoStepAuthentication({ jwt: localStorage.getItem("jwt"), otp })
    );
  };

  const handleVerifyOtp = (otp) => {
    console.log("otp  - ", otp);
    dispatch(verifyOtp({ jwt: localStorage.getItem("jwt"), otp }));
  };

  useEffect(() => {
    const jwt = localStorage.getItem("jwt");
    if (jwt) {
      dispatch(fetchUserProfile());
    }
  }, [dispatch]);

  useEffect(() => {
    if (auth.user) {
      console.log("User Profile Data:", auth.user);
      // DEBUG: Check if email is masked in state
      if (auth.user.email && auth.user.email.includes("***")) {
        console.error("CRITICAL: auth.user.email is masked! API calls using this email will fail.", auth.user.email);
      }
      setFormData({
        id: auth.user.id,
        email: auth.user.email,
        fullName: auth.user.fullName || "",
        dateOfBirth: auth.user.dateOfBirth || "",
        nationality: auth.user.nationality || "",
        address: auth.user.address || "",
        city: auth.user.city || "",
        postcode: auth.user.postcode || "",
        country: auth.user.country || "",
      });
      if (auth.user.picture) {
        setPreviewImage(auth.user.picture);
      }
    }
  }, [auth.user]);

  const handleFormChange = (e) => {
    const { name, value } = e.target;
    setFormData((prevState) => ({
      ...prevState,
      [name]: value,
    }));
  };

  // ✅ Handle image file selection and upload
  const handleImageChange = async (e) => {
    const file = e.target.files?.[0];
    if (!file) return;

    // Validate file type
    if (!file.type.startsWith("image/")) {
      alert("Please select a valid image file");
      return;
    }

    // Validate file size (5MB max)
    if (file.size > 5 * 1024 * 1024) {
      alert("Image size must be less than 5MB");
      return;
    }

    // Convert to base64 and compress
    const reader = new FileReader();
    reader.onloadend = async () => {
      let base64String = reader.result;
      if (file.size > 1 * 1024 * 1024) {
        // If larger than 1MB, compress
        base64String = await compressImage(base64String);
      }

      setPreviewImage(base64String);
      setImageLoading(true);

      try {
        console.log("Uploading image to backend...");
        console.log(
          "Image size:",
          Math.round(base64String.length / 1024),
          "KB"
        );
        const response = await dispatch(uploadProfileImage(base64String));
        console.log("Upload successful:", response);
        alert("Profile image uploaded successfully!");
        await dispatch(fetchUserProfile());
      } catch (error) {
        console.error("Upload error:", error);
        alert("Failed to upload image: " + error.message);
        setPreviewImage(auth.user?.picture || null);
      } finally {
        setImageLoading(false);
        if (fileInputRef.current) {
          fileInputRef.current.value = "";
        }
      }
    };
    reader.readAsDataURL(file);
  };

  // ✅ New: Compress image before upload
  const compressImage = (base64String) => {
    return new Promise((resolve) => {
      const img = new Image();
      img.src = base64String;
      img.onload = () => {
        const canvas = document.createElement("canvas");
        let width = img.width;
        let height = img.height;

        // Reduce dimensions by 50% if image is large
        if (width > 800 || height > 800) {
          const ratio = Math.min(800 / width, 800 / height);
          width = Math.round(width * ratio);
          height = Math.round(height * ratio);
        }

        canvas.width = width;
        canvas.height = height;

        const ctx = canvas.getContext("2d");
        ctx.drawImage(img, 0, 0, width, height);

        // Compress to JPEG with 80% quality
        const compressed = canvas.toDataURL("image/jpeg", 0.8);
        resolve(compressed);
      };
    });
  };

  // ✅ FIXED: Trigger file input when button is clicked
  const handleUploadButtonClick = () => {
    if (fileInputRef.current) {
      fileInputRef.current.click();
    }
  };

  const handleSave = async () => {
    try {
      const updateData = {
        ...formData,
        id: auth.user?.id,
        email: auth.user?.email
      };
      await dispatch(updateUserProfile(updateData));
      alert("Profile updated successfully!");
      setIsEditing(false);
      await dispatch(fetchUserProfile());
    } catch (error) {
      alert("Failed to update profile: " + error.message);
    }
  };

  if (auth.loading) return <SpinnerBackdrop />;

  return (
    <div className="flex flex-col items-center mb-5">
      <div className="pt-10 w-full lg:w-[60%]">
        {/* ✅ Profile Image Section */}
        <Card className="mb-6">
          <CardHeader className="pb-9">
            <CardTitle>Profile Picture</CardTitle>
          </CardHeader>
          <CardContent className="flex flex-col items-center gap-4">
            <div className="h-32 w-32 rounded-full bg-gradient-to-br from-slate-700 to-slate-900 overflow-hidden border-4 border-amber-500/50 flex items-center justify-center relative">
              {imageLoading && (
                <div className="absolute inset-0 bg-black/40 flex items-center justify-center rounded-full z-10">
                  <div className="text-white text-xs font-semibold">
                    Uploading...
                  </div>
                </div>
              )}
              {previewImage ? (
                <img
                  src={previewImage}
                  alt="Profile"
                  className="h-full w-full object-cover"
                />
              ) : (
                <div className="text-slate-400 text-center text-sm">
                  No Image
                </div>
              )}
            </div>

            {isEditing && (
              <div className="flex flex-col items-center gap-2">
                {/* ✅ Hidden file input with ref */}
                <input
                  ref={fileInputRef}
                  type="file"
                  accept="image/*"
                  onChange={handleImageChange}
                  className="hidden"
                />

                {/* ✅ Button triggers file input via onClick */}
                <Button
                  onClick={handleUploadButtonClick}
                  className="flex items-center gap-2"
                  disabled={imageLoading}
                >
                  <Upload className="h-4 w-4" />
                  {imageLoading ? "Uploading..." : "Upload Image"}
                </Button>
              </div>
            )}
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-9">
            <CardTitle>Your Information</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="lg:flex gap-32">
              <div className="space-y-7">
                <div className="flex items-center">
                  <p className="w-[9rem]">Email : </p>
                  <p className="text-gray-500">{auth.user?.email} </p>
                </div>
                <div className="flex items-center">
                  <p className="w-[9rem]">Full Name : </p>
                  {isEditing ? (
                    <Input
                      name="fullName"
                      value={formData.fullName || ""}
                      onChange={handleFormChange}
                      placeholder="Enter full name"
                    />
                  ) : (
                    <p className="text-gray-500">
                      {auth.user?.fullName || "Not Set"}
                    </p>
                  )}
                </div>
                <div className="flex items-center">
                  <p className="w-[9rem]">Date Of Birth : </p>
                  {isEditing ? (
                    <Input
                      type="date"
                      name="dateOfBirth"
                      value={formData.dateOfBirth || ""}
                      onChange={handleFormChange}
                    />
                  ) : (
                    <p className="text-gray-500">
                      {auth.user?.dateOfBirth || "Not Set"}
                    </p>
                  )}
                </div>
                <div className="flex items-center">
                  <p className="w-[9rem]">Nationality : </p>
                  {isEditing ? (
                    <Input
                      name="nationality"
                      value={formData.nationality || ""}
                      onChange={handleFormChange}
                      placeholder="Enter nationality"
                    />
                  ) : (
                    <p className="text-gray-500">
                      {auth.user?.nationality || "Not Set"}
                    </p>
                  )}
                </div>
              </div>
              <div className="space-y-7">
                <div className="flex items-center">
                  <p className="w-[9rem]">Address : </p>
                  {isEditing ? (
                    <Input
                      name="address"
                      value={formData.address || ""}
                      onChange={handleFormChange}
                      placeholder="Enter address"
                    />
                  ) : (
                    <p className="text-gray-500">
                      {auth.user?.address || "Not Set"}
                    </p>
                  )}
                </div>
                <div className="flex items-center">
                  <p className="w-[9rem]">City : </p>
                  {isEditing ? (
                    <Input
                      name="city"
                      value={formData.city || ""}
                      onChange={handleFormChange}
                      placeholder="Enter city"
                    />
                  ) : (
                    <p className="text-gray-500">
                      {auth.user?.city || "Not Set"}
                    </p>
                  )}
                </div>
                <div className="flex items-center">
                  <p className="w-[9rem]">Postcode : </p>
                  {isEditing ? (
                    <Input
                      name="postcode"
                      value={formData.postcode || ""}
                      onChange={handleFormChange}
                      placeholder="Enter postcode"
                    />
                  ) : (
                    <p className="text-gray-500">
                      {auth.user?.postcode || "Not Set"}
                    </p>
                  )}
                </div>
                <div className="flex items-center">
                  <p className="w-[9rem]">Country : </p>
                  {isEditing ? (
                    <Input
                      name="country"
                      value={formData.country || ""}
                      onChange={handleFormChange}
                      placeholder="Enter country"
                    />
                  ) : (
                    <p className="text-gray-500">
                      {auth.user?.country || "Not Set"}
                    </p>
                  )}
                </div>
              </div>
            </div>
            <div className="flex justify-end mt-8">
              {isEditing ? (
                <div className="flex gap-4">
                  <Button
                    variant="outline"
                    onClick={() => {
                      setIsEditing(false);
                      setPreviewImage(auth.user?.picture || null);
                      if (fileInputRef.current) {
                        fileInputRef.current.value = "";
                      }
                    }}
                  >
                    Cancel
                  </Button>
                  <Button onClick={handleSave}>Save Changes</Button>
                </div>
              ) : (
                <Button onClick={() => setIsEditing(true)}>Edit Profile</Button>
              )}
            </div>
          </CardContent>
        </Card>

        <div className="mt-6">
          <Card className="w-full">
            <CardHeader className="pb-7">
              <div className="flex items-center gap-3">
                <CardTitle>2 Step Verification</CardTitle>

                {auth.user?.twoFactorAuth?.enabled ? (
                  <Badge className="space-x-2 text-white bg-green-600">
                    <VerifiedIcon /> <span>Enabled</span>
                  </Badge>
                ) : (
                  <Badge className="bg-orange-500">Disabled</Badge>
                )}
              </div>
            </CardHeader>
            <CardContent className="space-y-5">
              <div>
                <Dialog>
                  <DialogTrigger>
                    <Button>Enable Two Step Verification</Button>
                  </DialogTrigger>
                  <DialogContent>
                    <DialogHeader>
                      <DialogTitle className="px-10 pt-5 text-center">
                        Verify Your Account
                      </DialogTitle>
                    </DialogHeader>
                    <AccountVarificationForm
                      handleSubmit={handleEnableTwoStepVerification}
                    />
                  </DialogContent>
                </Dialog>
              </div>
            </CardContent>
          </Card>
        </div>

        <div className="lg:flex gap-5 mt-5">
          <Card className="w-full">
            <CardHeader className="pb-7">
              <CardTitle>Change Password</CardTitle>
            </CardHeader>
            <CardContent className="space-y-5">
              <div className="flex items-center">
                <p className="w-[8rem]">Email :</p>
                <p>{auth.user?.email}</p>
              </div>
              <div className="flex items-center">
                <p className="w-[8rem]">Password :</p>
                <Dialog>
                  <DialogTrigger asChild>
                    <Button variant="secondary">Change Password</Button>
                  </DialogTrigger>
                  <DialogContent>
                    <DialogHeader>
                      <DialogTitle className="px-10 pt-5 text-center">Change Password</DialogTitle>
                    </DialogHeader>
                    <div className="space-y-3">
                      <Input placeholder="Current Password" type="password" />
                      <Input placeholder="New Password" type="password" />
                      <Button className="w-full">Update Password</Button>
                    </div>
                  </DialogContent>
                </Dialog>
              </div>
            </CardContent>
          </Card>
          <Card className="w-full">
            <CardHeader className="pb-7">
              <div className="flex items-center gap-3">
                <CardTitle>Account Status</CardTitle>

                {auth.user?.verified ? (
                  <Badge className="space-x-2 text-white bg-green-600">
                    <VerifiedIcon /> <span>Verified</span>
                  </Badge>
                ) : (
                  <Badge className="bg-orange-500">Pending</Badge>
                )}
              </div>
            </CardHeader>
            <CardContent className="space-y-5">
              <div className="flex items-center">
                <p className="w-[8rem]">Email :</p>
                <p>{auth.user?.email}</p>
              </div>
              <div className="flex items-center">
                <p className="w-[8rem]">Mobile :</p>
                <p>{auth.user?.mobile || "+918987667899"}</p>
              </div>
              <div>
                <Dialog>
                  <DialogTrigger>
                    <Button>Verify Account</Button>
                  </DialogTrigger>
                  <DialogContent>
                    <DialogHeader>
                      <DialogTitle className="px-10 pt-5 text-center">
                        Verify Your Account
                      </DialogTitle>
                    </DialogHeader>
                    <AccountVarificationForm handleSubmit={handleVerifyOtp} />
                  </DialogContent>
                </Dialog>
              </div>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
};

export default Profile;
